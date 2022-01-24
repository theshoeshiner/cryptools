package org.thshsh.crypt.serv;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.cryptocompare.CryptoCompareException;
import org.thshsh.crypt.cryptocompare.CurrentPricesResponse;
import org.thshsh.crypt.cryptocompare.HistoricalPrices;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.MarketRateRepository;

@Service
@Component
@Transactional
public class MarketRateService {

	public static final Logger LOGGER = LoggerFactory.getLogger(MarketRateService.class);

	public static final Long HOUR_SECONDS = Duration.ofHours(1).getSeconds();
	public static final Duration HOUR = Duration.ofHours(1);
	public static final Duration HALF_HOUR = Duration.ofMinutes(30);
	public static final Duration MIN_20 = Duration.ofMinutes(20);
	
	@Autowired
	MarketRateRepository rateRepo;

	@Autowired
	CurrencyRepository currRepo;

	@Autowired
	CryptoCompare compare;

	Currency usd;
	MarketRate usdRate;

	Duration ageRadius = MIN_20;
	//only prefer a ranged quote if it is this much closer to the time
	Duration rangePreferenceThreshold = ageRadius.dividedBy(2);

	@PostConstruct
	public void postConstruct() {
		usd = currRepo.findByKey("USD");
		usdRate = rateRepo.findTopByCurrencyOrderByTimestampDesc(usd);
		if(usdRate == null) {
			usdRate = new MarketRate(usd, BigDecimal.ONE, ZonedDateTime.now());
			rateRepo.save(usdRate);
		}
		
	}

	/**
	 * Looks for market rates that are less than one hour old
	 * @param currencies
	 * @return
	 */
	
	/*public MarketRate getUpToDateMarketRate(String apiKey,Currency c){
		return getUpToDateMarketRates(apiKey, c).get(c);
	}
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Currency... currencies){
		return getUpToDateMarketRates(apiKey, Arrays.asList(currencies));
	}
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Collection<Currency> currencies){
		return getUpToDateMarketRates(apiKey, currencies, false);
	}
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Collection<Currency> currencies, Boolean force){*/
	
	public MarketRate getUpToDateMarketRate(String apiKey,Currency c){
		return getUpToDateMarketRates(apiKey, c).get(c);
	}
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Currency... currencies){
		return getUpToDateMarketRates(apiKey, Arrays.asList(currencies));
	}
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Collection<Currency> currencies){
		return getMarketRates(apiKey, currencies, false,null);
	}

	//@Transactional(value = TxType.REQUIRES_NEW)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<Currency,MarketRate> getMarketRates(ZonedDateTime time,Currency... currencies){
		return getMarketRates(null, Arrays.asList(currencies),false,time);
	}
	
	//@Transactional(value = TxType.REQUIRES_NEW)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<Currency,MarketRate> getMarketRates(String apiKey,Collection<Currency> currencies, Boolean force, ZonedDateTime t){

		LOGGER.info("getUpToDateMarketRates time: {} currencies: {}",t,currencies);
		
		currencies = new ArrayList<>(currencies);
		currencies.remove(usd);
		ZonedDateTime time = t==null?ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z")):t;
		Boolean current = t == null;
		

		Map<Currency,List<MarketRate>> ratesListMap = new HashMap<Currency, List<MarketRate>>();
		Map<Currency,MarketRate> marketRateMap = new HashMap<>();
		List<Currency> getRatesFor = new ArrayList<>();

		if(!force) {
		
			ZonedDateTime minAge = time.minus(ageRadius);
			ZonedDateTime maxAage = time.plus(ageRadius);
	
			currencies.forEach(cur -> {
				
				//if we have rates within the radius then we use those
				List<MarketRate> rates = new ArrayList<>(rateRepo.findByCurrencyAndTimestampGreaterThanAndTimestampLessThanOrderByTimestampDesc(cur, minAge, maxAage));
	
				if(rates.size() > 0) {
					
					LOGGER.info("Found rates in database: {}",rates);
					ratesListMap.put(cur, rates);
					
					
				}
				else {
					getRatesFor.add(cur);
				}
	
				
			});
		
		}
		else {
			getRatesFor.addAll(currencies);
		}

		marketRateMap.put(usd, usdRate);

		LOGGER.info("Need to get rates for: {}",getRatesFor);

		if(getRatesFor.size() > 0) {

			//String oldApi = compare.getApiKey();
			try {
				if(apiKey != null) compare.getApiKeyThreadLocal().set(apiKey);
				
				Map<String,Currency> symMap = getRatesFor.stream().collect(Collectors.toMap(Currency::getKey, Function.identity()));
				
				if(current) {
					
					CurrentPricesResponse prices = compare.getCurrentPrice(usd.getKey(),symMap.keySet());
					symMap.keySet().forEach(sym -> {
						BigDecimal price = prices.getPrice(sym);
						LOGGER.info("price: {}",price);
						Currency c = symMap.get(sym);
						MarketRate mr = new MarketRate(c,price,time);
						rateRepo.save(mr);
						marketRateMap.put(c, mr);
					});
					
				}
				else {
					
					symMap.keySet().forEach(sym -> {
						Currency cur = symMap.get(sym);
						MutableBoolean first = new MutableBoolean(true);
						HistoricalPrices hps = compare.getHourlyHistoricalPrice(sym,usd.getKey(), time.withZoneSameInstant( ZoneId.of("Z")).toEpochSecond());
						//each row will create at least 2 rates, which we will then check to see which is closest
						List<MarketRate> rates = new ArrayList<>();
						hps.getHistoricalPrices().forEach(hp -> {
							ZonedDateTime startTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(hp.getTime().longValue()), ZoneId.of("Z"));
							ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(hp.getTime().longValue()+HOUR_SECONDS), ZoneId.of("Z"));
							if(first.booleanValue()) {
								//only generate the open rate on the first row
								MarketRate openRate = new MarketRate(cur,hp.getOpen(),startTime);
								//LOGGER.info("Open Rate: {}",openRate);
								rates.add(openRate);
								first.setFalse();
							}
							MarketRate rangeRate = new MarketRate(cur,hp.getHigh(),hp.getLow(),hp.getOpen(),hp.getClose(),startTime,endTime);
							rates.add(rangeRate);
							//LOGGER.info("Avg Rate: {}",rangeRate);
							MarketRate closeRate = new MarketRate(cur,hp.getClose(),endTime);
							rates.add(closeRate);
							//LOGGER.info("Close Rate: {}",closeRate);
							rateRepo.saveAll(rates);
						});
						ratesListMap.put(cur, rates);
					});

				}
				

			}
			catch(CryptoCompareException ex) {
				LOGGER.warn("Error getting rates",ex);
			}

		}
		
		
		//now go through rates
		
		//now sort ratelists and find best
		ratesListMap.forEach((cur,list) -> {
			
			MarketRate closest;
			
			Collections.sort(list, (r0,r1) -> {
				/*if(r0.isRange() && !r1.isRange()) {
					//if r1 rate is closer than limit, then prefer it over r0
					if(Duration.between(r1.getTimestamp(), time).abs().compareTo(rangePreferenceLimit)<0) return 1;
					//else if r0 is closer than limit, then prefer it
					else if(Duration.between(r0.getTimestamp(), time).abs().compareTo(rangePreferenceLimit)<0) return -1;
				}
				else if(!r0.isRange() && r1.isRange()) {
					//just the reverse of the above
					if(Duration.between(r0.getTimestamp(), time).abs().compareTo(rangePreferenceLimit)<0) return -1;
					else if(Duration.between(r1.getTimestamp(), time).abs().compareTo(rangePreferenceLimit)<0) return 1;
				}*/
				//just prefer the rate that is closest
				return Duration.between(r0.getTimestamp(), time).abs().compareTo(Duration.between(r1.getTimestamp(), time).abs());
			});
			//sort rates based on distance from timestamp
			LOGGER.info("sorted market rates: {}",list);
			
			Optional<MarketRate> ranged = list.stream().filter(mr -> mr.isRange()).findFirst();
			Optional<MarketRate> specific = list.stream().filter(mr -> !mr.isRange()).findFirst();
			//LOGGER.info("specific: {}",specific);
			//LOGGER.info("ranged: {}",ranged);
			if(ranged.isPresent() && specific.isPresent()) {
				//both are present, only use the ranged one if specific one is past threshold
				MarketRate specificRate = specific.get();
				MarketRate rangedRate = ranged.get();
				//LOGGER.info("specific: {}",specificRate);
				//LOGGER.info("ranged: {}",rangedRate);
				Duration specificDuration = Duration.between(time,specificRate.getTimestamp()).abs();
				Duration rangedDuration = Duration.between(time,rangedRate.getTimestamp()).abs();
				//LOGGER.info("specificDuration: {}",specificDuration);
				//LOGGER.info("rangedDuration: {}",rangedDuration);
				if(specificDuration.compareTo(rangedDuration) < 0 || specificDuration.compareTo(rangePreferenceThreshold) < 0) {
					closest = specificRate;
				}
				else {
					closest = rangedRate;
				}
			}
			else {
				closest = ranged.orElseGet(() -> specific.get());
			}
			
			
			
			//MarketRate lastRate = list.get(0);
			LOGGER.info("closest rate: {}",closest);
			marketRateMap.put(cur, closest);
			
		});

		rateRepo.flush();
		
		return marketRateMap;

	}

	

}
