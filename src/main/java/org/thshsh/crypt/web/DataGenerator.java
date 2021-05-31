package org.thshsh.crypt.web;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.ExchangeRepository;
import org.thshsh.crypt.cryptocompare.Coin;
import org.thshsh.crypt.cryptocompare.CryptoCompare;

@Component
public class DataGenerator {

	public static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

	@Autowired
	ExchangeRepository exchangeRepo;

	@Autowired
	CurrencyRepository currencyRepo;

	@Autowired
	CryptoCompare compare;

	@PostConstruct
	public void postConstruct() {


		Map<String,Exchange> idMap = exchangeRepo.findAll().stream().collect(Collectors.toMap(Exchange::getRemoteId,Function.identity()));

		compare.getExchanges().forEach(ex -> {
			if(!idMap.containsKey(ex.getId())) {
				Exchange e = new Exchange(ex.getName(), ex.getId(), ex.getInternalName());
				LOGGER.info("Creating Exchange: {}",e);
				exchangeRepo.save(e);
			}
		});

		Map<String,Currency> currMap = currencyRepo.findAll().stream().collect(Collectors.toMap(Currency::getRemoteId,Function.identity()));



		Collection<Coin> coins = compare.getCoins();
		coins.forEach(coin -> {
			Currency c = currMap.get(coin.getId());
			if(c == null) c = new Currency(null, null, coin.getId());
			c.setName(coin.getName());
			c.setSymbol(coin.getSymbol());
			if(coin.getBuiltOn()!=null) {
				Currency b = currencyRepo.findBySymbol(coin.getBuiltOn());
				c.setBuiltOn(b);

			}
			currencyRepo.save(c);
		});


	}

}
