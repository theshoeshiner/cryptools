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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.thshsh.crypt.cryptocompare.Coin;
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

	//radius of considering a quote uptodate
	Duration ageRadius = MIN_20;
	
	//only prefer a ranged quote if its mid-point is much closer to the target time
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<Currency,MarketRate> getMarketRates(ZonedDateTime time,Currency... currencies){
		return getMarketRates(null, Arrays.asList(currencies),false,time);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<Currency,MarketRate> getMarketRates(String apiKey,Collection<Currency> currencies, Boolean force, ZonedDateTime t){

		LOGGER.info("getUpToDateMarketRates time: {} currencies: {}",t,currencies);
		
		currencies = new ArrayList<>(currencies);
		currencies.remove(usd);
		ZonedDateTime time = t==null?ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z")):t;
		Boolean current = t == null;
		

		Map<Currency,List<MarketRate>> ratesListMap = new HashMap<Currency, List<MarketRate>>();
		Map<Currency,MarketRate> marketRateMap = new HashMap<>();
		marketRateMap.put(usd, usdRate);
		List<Currency> getRatesFor = new ArrayList<>();

		if(!force) {
		
			//if we're not forcing all then first try and get rates from the database
			
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

		

		LOGGER.info("Need to request rates for: {}",getRatesFor);

		if(getRatesFor.size() > 0) {

			//String oldApi = compare.getApiKey();
			try {
				if(apiKey != null) compare.getApiKeyThreadLocal().set(apiKey);
				
				//create map of any currencies with symbol collisions
				Map<String,Collection<Currency>> collMap = new HashMap<String, Collection<Currency>>();
				getRatesFor.stream().collect(Collectors.toMap(
						Currency::getKey, Function.identity(),(c0,c1) -> {
							LOGGER.error("There was a collision for symbol: {}",c0.getKey());
							if(!collMap.containsKey(c0.getKey())) collMap.put(c0.getKey(),new HashSet<>());
							collMap.get(c0.getKey()).add(c0);
							collMap.get(c0.getKey()).add(c1);
							return c0;
						}
				));
				
				//figure out which collision currencies are official
				Map<String,Currency> collOfficial = new HashMap<String, Currency>();
				collMap.forEach((key,coll) -> {
					//there is a symbol need to decide which is the official
					Coin coin = compare.getCoin(key, true);
					Currency match = collMap.get(key).stream().filter(c -> coin.getId().equals(c.getRemoteId())).findFirst().orElse(null);
					collOfficial.put(key, match);
				});
				
				//create set of all symbols
				Set<String> symbols = getRatesFor.stream().map(c -> c.getKey()).collect(Collectors.toSet());
				
				if(current) {
					
					CurrentPricesResponse prices = compare.getCurrentPrice(usd.getKey(),symbols);
					getRatesFor.forEach(curr -> {
						String sym = curr.getKey();
						
						//if this symbol has a collision and is not the current official, then dont use price
						if(collOfficial.containsKey(sym) && !curr.equals(collOfficial.get(sym))) {
							//TODO should we set the non match as inactive, or change the symbol somehow?
						}
						else {
							BigDecimal price = prices.getPrice(sym);
							LOGGER.info("price: {}",price);
							if(price != null) {
								MarketRate mr = new MarketRate(curr,price,time);
								rateRepo.save(mr);
								marketRateMap.put(curr, mr);
							}
						}
					});
					
				}
				else {
					
					//we are grabbing a historical rate - this doesnt happen for the hourly job
					
					getRatesFor.forEach(cur -> {
						//Currency cur = symMap.get(sym);
						String sym = cur.getKey();
						MutableBoolean first = new MutableBoolean(true);
						HistoricalPrices hps = compare.getHourlyHistoricalPrice(sym,usd.getKey(), time.withZoneSameInstant( ZoneId.of("Z")).toEpochSecond());
						//each row will create at least 2 rates, which we will then check to see which is closest
						
						//if this symbol has a collision and is not the current official, then dont use any rates
						if(collOfficial.containsKey(sym) && !cur.equals(collOfficial.get(sym))) {
							//no-op
						}
						else {
							List<MarketRate> rates = new ArrayList<>();
							hps.getHistoricalPrices().forEach(hp -> {
								ZonedDateTime startTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(hp.getTime().longValue()), ZoneId.of("Z"));
								ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(hp.getTime().longValue()+HOUR_SECONDS), ZoneId.of("Z"));
								if(first.booleanValue()) {
									//only generate the open rate on the first row
									MarketRate openRate = new MarketRate(cur,hp.getOpen(),startTime);
									rates.add(openRate);
									first.setFalse();
								}
								MarketRate rangeRate = new MarketRate(cur,hp.getHigh(),hp.getLow(),hp.getOpen(),hp.getClose(),startTime,endTime);
								rates.add(rangeRate);
								MarketRate closeRate = new MarketRate(cur,hp.getClose(),endTime);
								rates.add(closeRate);
								rateRepo.saveAll(rates);
							});
							ratesListMap.put(cur, rates);
						}
					});

				}
				

			}
			catch(CryptoCompareException ex) {
				LOGGER.warn("Error getting rates",ex);
			}

		}
		
		
		//now go through rate lists if this was historical or we found rates in the DB
		
		//now sort ratelists and find best
		ratesListMap.forEach((cur,list) -> {
			
			MarketRate closest;
			
			Collections.sort(list, (r0,r1) -> {
				//just prefer the rate that is closest
				return Duration.between(r0.getTimestamp(), time).abs().compareTo(Duration.between(r1.getTimestamp(), time).abs());
			});
			//sort rates based on distance from timestamp
			LOGGER.info("sorted market rates: {}",list);
			
			Optional<MarketRate> ranged = list.stream().filter(mr -> mr.isRange()).findFirst();
			Optional<MarketRate> specific = list.stream().filter(mr -> !mr.isRange()).findFirst();
			if(ranged.isPresent() && specific.isPresent()) {
				//both are present, only use the ranged one if specific one is past threshold
				MarketRate specificRate = specific.get();
				MarketRate rangedRate = ranged.get();
				Duration specificDuration = Duration.between(time,specificRate.getTimestamp()).abs();
				Duration rangedDuration = Duration.between(time,rangedRate.getTimestamp()).abs();
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

			LOGGER.info("closest rate: {}",closest);
			marketRateMap.put(cur, closest);
			
		});

		rateRepo.flush();
		
		return marketRateMap;

	}

	

}
