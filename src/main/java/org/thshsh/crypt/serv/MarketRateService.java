package org.thshsh.crypt.serv;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.cryptocompare.CryptoCompareException;
import org.thshsh.crypt.cryptocompare.CurrentPricesResponse;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.MarketRateRepository;

@Service
@Component
public class MarketRateService {

	public static final Logger LOGGER = LoggerFactory.getLogger(MarketRateService.class);

	@Autowired
	MarketRateRepository rateRepo;

	@Autowired
	CurrencyRepository currRepo;

	@Autowired
	CryptoCompare compare;

	static Duration HOUR = Duration.ofHours(1);

	Currency usd;
	MarketRate usdRate;

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
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Collection<Currency> currencies){
		return getUpToDateMarketRates(apiKey, currencies, false);
	}
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Collection<Currency> currencies, Boolean force){

		LOGGER.info("getUpToDateMarketRates api: {} currencies: {}",apiKey,currencies);
		
		currencies = new ArrayList<>(currencies);

		currencies.remove(usd);

		Map<Currency,MarketRate> marketRateMap = new HashMap<>();

		/*currencies.forEach(c -> {
			MarketRate mr = rateRepo.findTopByCurrencyOrderByTimestampDesc(c);
		});
		*/
		//Map<Currency,MarketRate> map = rateRepo.findByCurrencyIn(currencies).stream().collect(Collectors.toMap(MarketRate::getCurrency, Function.identity()));

		//List<String> symbols = currencies.stream().map(Currency::getSymbol).collect(Collectors.toList());

		ZonedDateTime now = ZonedDateTime.now();

		ZonedDateTime hourAgo = now.minus(HOUR);

		List<Currency> getRatesFor = new ArrayList<>();

		currencies.forEach(cur -> {

			MarketRate lastRate = rateRepo.findTopByCurrencyOrderByTimestampDesc(cur);

			if(lastRate == null || force) {
				getRatesFor.add(cur);
			}
			else if(!lastRate.getTimestamp().isAfter(hourAgo)) {
				getRatesFor.add(cur);
			}
			else marketRateMap.put(cur, lastRate);
		});

		marketRateMap.put(usd, usdRate);

		LOGGER.info("Need to get rates for: {}",getRatesFor);

		if(getRatesFor.size() > 0) {

			String oldApi = compare.getApiKey();
			try {
				if(apiKey != null) compare.setApiKey(apiKey);
				Map<String,Currency> symMap = getRatesFor.stream().collect(Collectors.toMap(Currency::getKey, Function.identity()));
				CurrentPricesResponse prices = compare.getCurrentPrice(usd.getKey(),symMap.keySet());
				symMap.keySet().forEach(sym -> {
					BigDecimal price = prices.getPrice(sym);
					Currency c = symMap.get(sym);
					MarketRate mr = new MarketRate(c,price,now);
					rateRepo.save(mr);
					marketRateMap.put(c, mr);
				});
				/*prices.forEach((s,v) -> {
					Currency c = symMap.get(s);
					MarketRate mr = new MarketRate(c,v,now);
					rateRepo.save(mr);
					map.put(c, mr);
				});*/
			}
			catch(CryptoCompareException ex) {
				LOGGER.warn("Error getting rates",ex);
			}
			finally {
				compare.setApiKey(oldApi);
			}

		}

		return marketRateMap;

	}

	

}
