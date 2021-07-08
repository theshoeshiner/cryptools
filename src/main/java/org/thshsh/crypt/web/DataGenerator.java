package org.thshsh.crypt.web;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.ExchangeRepository;
import org.thshsh.crypt.PlatformType;
import org.thshsh.crypt.UserRepository;
import org.thshsh.crypt.cryptocompare.Coin;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.serv.UserService;

@Component
public class DataGenerator {

	public static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

	@Autowired
	AppConfiguration appConfig;

	@Autowired
	ExchangeRepository exchangeRepo;

	@Autowired
	CurrencyRepository currencyRepo;

	@Autowired
	CryptoCompare compare;

	@Autowired
	AsyncTaskExecutor executor;

	@Autowired
	DataConfiguration dataConfig;

	@Autowired
	CurrencyService curService;

	@Autowired
	ImageService imageService;

	@Autowired
	UserRepository userRepo;

	@Autowired
	UserService userService;

	@Autowired
	PlatformTransactionManager transactionManager;

	TransactionTemplate template;

	Map<String,Currency> currMap;

	@PostConstruct
	public void postConstruct() {

		template = new TransactionTemplate(transactionManager);

		if(!appConfig.getProductionMode()) {
			executor.execute(this::passwords);
		}

		if(appConfig.getCryptoCompareSync()) {

			executor.execute(this::exchanges);


			CompletableFuture
				.runAsync(this::currencies, executor)
				.thenRunAsync(this::fiat, executor);

		}

	}

	protected void fiat() {

		template.executeWithoutResult(action -> {

			for(List<String> cur : dataConfig.getFiatcurrencies()) {
				LOGGER.info("checking for {}",cur);
				Currency c = currencyRepo.findByKey(cur.get(1));
				if(c == null) c = new Currency(cur.get(0),cur.get(1),PlatformType.fiat);
				else {
					c.setName(cur.get(0));
					c.setPlatformType(PlatformType.fiat);
				}
				LOGGER.info("saving: {}",c);
				currencyRepo.save(c);

			}


		});
	}

	protected void currency(Coin coin) {

		template.executeWithoutResult(action -> {

			Currency c = currMap.get(coin.getId());
			if(c == null) c = new Currency(null, null, coin.getId());
			c.setName(coin.getName());
			c.setKey(coin.getSymbol());

			if(coin.getBuiltOn()!=null) {
				Currency b = currencyRepo.findByKey(coin.getBuiltOn());
				c.setBuiltOn(b);
			}
			c.setPlatformType(coin.getPlatformType()==null?null:PlatformType.valueOf(coin.getPlatformType()));


			try {
				imageService.syncImage(c, coin.getImageUrl());

			}
			catch (IOException ioe) {
				LOGGER.error("",ioe);
			}

			/*if(coin.getImageUrl() != null) {
				if(c.getImageUrl() == null) {
					File imagepath = new File(coin.getImageUrl());
					try {
						InputStream is = curService.getImage(imagepath.getName());
						if(is != null) {
							LOGGER.info("Image {} already existed",imagepath);
							c.setImageUrl(imagepath.getName());
						}
						else {
							InputStream remoteImage = compare.getImage(coin);
							curService.saveImage(c, remoteImage, imagepath.getName());
						}
					}
					catch (IOException e) {
						LOGGER.error("error",e);
					}
				}
			}*/

			currencyRepo.save(c);

		});

	}

	protected void currencies() {

		currMap = currencyRepo.findByRemoteIdNotNull().stream().collect(Collectors.toMap(Currency::getRemoteId,Function.identity()));

		Collection<Coin> coins = compare.getCoins();
		coins.forEach(coin -> {
			currency(coin);
		});



	}

	protected void passwords() {

		template.executeWithoutResult(action -> {

			userRepo.findAll().forEach(user -> {
				if(user.getPassword().equals("dev")) {
					userService.setPassword(user, "dev");
				}
			});

		});

	}

	protected void exchanges() {

		Map<String,Exchange> idMap = exchangeRepo.findAll().stream().collect(Collectors.toMap(
				Exchange::getRemoteId,
				Function.identity(),
				(k0,k1) -> {
					return k0;
				}
				));

		compare.getExchanges().forEach(ex -> {

			template.executeWithoutResult(action -> {
				Exchange e;
				if(!idMap.containsKey(ex.getId())) {
					e = new Exchange(ex.getName(), ex.getId(), ex.getInternalName(),null);
					LOGGER.info("Creating Exchange: {}",e);

				}
				else {
					e = idMap.get(ex.getId());
				}
				e.setKey(e.getName().replaceAll("[^\\p{Alnum}]+?", "").toLowerCase());
				try {
					imageService.syncImage(e, ex.getLogoUrl());
				}
				catch (IOException ioe) {
					LOGGER.error("",ioe);
				}
				exchangeRepo.save(e);
			});

		});

	}

}
