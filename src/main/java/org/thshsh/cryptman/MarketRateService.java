package org.thshsh.cryptman;

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
import org.thshsh.crypt.cryptocompare.CryptoCompare;
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
		usdRate = new MarketRate(usd, BigDecimal.ONE, null);
	}

	/**
	 * Looks for market rates that are less than one hour old
	 * @param currencies
	 * @return
	 */
	
	public Map<Currency,MarketRate> getUpToDateMarketRates(String apiKey,Collection<Currency> currencies){

		LOGGER.info("getUpToDateMarketRates api: {} currencies: {}",apiKey,currencies);
		
		currencies = new ArrayList<>(currencies);

		currencies.remove(usd);

		Map<Currency,MarketRate> map = new HashMap<>();

		/*currencies.forEach(c -> {
			MarketRate mr = rateRepo.findTopByCurrencyOrderByTimestampDesc(c);
		});
		*/
		//Map<Currency,MarketRate> map = rateRepo.findByCurrencyIn(currencies).stream().collect(Collectors.toMap(MarketRate::getCurrency, Function.identity()));

		//List<String> symbols = currencies.stream().map(Currency::getSymbol).collect(Collectors.toList());

		ZonedDateTime now = ZonedDateTime.now();

		ZonedDateTime hourAgo = now.minus(HOUR);

		List<Currency> get = new ArrayList<>();

		currencies.forEach(cur -> {

			MarketRate mr = rateRepo.findTopByCurrencyOrderByTimestampDesc(cur);

			if(mr == null) {
				get.add(cur);
			}
			else if(!mr.getTimestamp().isAfter(hourAgo)) {
				get.add(cur);
			}
			else map.put(cur, mr);
		});

		map.put(usd, usdRate);

		LOGGER.info("Need to get rates for: {}",get);

		if(get.size() > 0) {

			String oldApi = compare.getApiKey();
			try {
				if(apiKey != null) compare.setApiKey(apiKey);
				Map<String,Currency> symMap = get.stream().collect(Collectors.toMap(Currency::getKey, Function.identity()));
				Map<String,BigDecimal> prices = compare.getCurrentFiatPrice(symMap.keySet());
	
				prices.forEach((s,v) -> {
					Currency c = symMap.get(s);
					MarketRate mr = new MarketRate(c,v,now);
					rateRepo.save(mr);
					map.put(c, mr);
				});
			}
			finally {
				compare.setApiKey(oldApi);
			}

		}

		return map;

	}

}
