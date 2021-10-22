package org.thshsh.crypt.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.color.AbstractColor;
import org.thshsh.color.ColorSpaceConverter;
import org.thshsh.color.ColorUtils;
import org.thshsh.color.LchColor;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Grade;
import org.thshsh.crypt.Permission;
import org.thshsh.crypt.PlatformType;
import org.thshsh.crypt.Role;
import org.thshsh.crypt.cryptocompare.Coin;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.cryptocompare.ExchangePairs;
import org.thshsh.crypt.cryptocompare.ExchangesCurrencyPairs;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.serv.CurrencyService;
import org.thshsh.crypt.serv.ImageService;
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
	RoleRepository roleRepo;

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

		//CompletableFuture<?> future = CompletableFuture.runAsync(() -> {}, executor);
		//future.complete(null);
		
		
		if(!appConfig.getProductionMode()) {
		
			 CompletableFuture.runAsync(this::resetDevPasswords, executor);
			//future = future.thenRunAsync(this::resetDevPasswords);
			//executor.execute(this::resetDevPasswords);
			
			//executor.execute(this::checkForLocalImages);
			
			/*future = CompletableFuture
			.runAsync(this::checkForLocalImages, executor)
			.thenRunAsync(this::getColors, executor);*/
		}
		else {
			
		}
		
		CompletableFuture<?> future = CompletableFuture.runAsync(() -> {}, executor);
		
		future = future.thenRunAsync(this::updateRoles);

		if(appConfig.getCryptocompare().getExchanges()) {
			future = future.thenRunAsync(this::syncExchanges,executor);
		}
		
		if(appConfig.getCryptocompare().getCoins()) {
			future = future.thenRunAsync(this::syncCurrencies,executor);
		}
		
		if(appConfig.getCryptocompare().getGrades()) {
			future = future.thenRunAsync(this::syncGrades,executor);
		}
		
		if(appConfig.getCryptocompare().getImages()) {
			future = future
					.thenRunAsync(this::checkForLocalImages, executor)
					.thenRunAsync(this::getColors, executor);
		}
		
		//if(appConfig.getCryptoCompareSync()) {
			
			
			

			//if(images != null) {
				/*future
				.thenRunAsync(this::updateCurrencies, executor)
				.thenRunAsync(this::fiat, executor);*/
			//}
			/*CompletableFuture
				.runAsync(this::currencies, executor)
				.thenRunAsync(this::fiat, executor);*/
		//}
		
		

	}
	
	protected void checkForLocalImages() {
		
		template.executeWithoutResult(action -> {
			
			currencyRepo.findAll().forEach(cur -> {
				if(cur.getImageUrl() == null) {
					String checkName = cur.getKey().toLowerCase()+".png";
					if(imageService.checkForImage(checkName)) {
						cur.setImageUrl(checkName);
					}
				}
			});
			
		});
	}
	
	protected void getColors() {

			List<Currency> save = new ArrayList<>();
			
			ColorSpaceConverter csc = new ColorSpaceConverter();
			currencyRepo.findAll().forEach(cur -> {
				
				if(Thread.currentThread().isInterrupted()) return;
				
				template.executeWithoutResult(action -> {
				
				
					
				if(StringUtils.isNotBlank(cur.getColorHex())
				//&& !cur.getColorHex().equals("000000")		
				) return;
				
				//LOGGER.info("checking image for: {}",cur.getKey());
				try {
					LOGGER.info("image: {}",cur.getImageUrl());
					InputStream image = imageService.getImage(cur);
					
					if(image == null) return;
					
					LOGGER.info("checking image for: {} = {}",cur.getKey(),cur.getColorHex());
					
					BufferedImage bi = null;
					try {
						bi = Imaging.getBufferedImage(image);
					}
					catch(Exception iae) {
						//LOGGER.error("",iae);
						image = imageService.getImage(cur);
						try {
							bi = ImageIO.read(image);
						} 
						catch (Exception e) {
							//LOGGER.error("",e);
						}
					}
					
					if(bi == null) {
						LOGGER.error("Could not load image");
						return;
						//throw new IllegalStateException("Could not load image");
					}
					
		
					List<AbstractColor> toAvg = new ArrayList<>();
					for(int x=0;x<bi.getWidth();x++) {
						for(int y=0;y<bi.getHeight();y++) {
							int color = bi.getRGB(x, y);
							
							LchColor lch = new LchColor(csc.InttoLCH(color));
							//CieLabColor c = new CieLabColor(csc.InttoLAB(color));
								/*if(cur.getKey().equalsIgnoreCase("usd")) {
									LOGGER.info("color: {}",lch); 
								}*/
							if(lch.getL()<90 && lch.getL() > 10) {
								
								if(lch.getC() > 10) {
								//LOGGER.info("color: {}",lch);
									toAvg.add(lch);
								}
							}
							
							
						}
					}
					
					if(toAvg.size()>0) {
						LchColor average = (LchColor) ColorUtils.averageColors(toAvg);
						LOGGER.info("average: {}",average); 
						int[] rgb = csc.LABtoRGB(csc.LCHtoLAB(average.getComponentsPrimitive()));
						byte[] rbgb = new byte[] {(byte) rgb[0],(byte) rgb[1],(byte) rgb[2]};
						LOGGER.info("rbg: {}",new Object[] {rgb}); 
						String hex = Hex.encodeHexString(rbgb);
						LOGGER.info("hex: {}",hex);
						cur.setColorHex(hex);
						save.add(cur);
						currencyRepo.save(cur);
					}
					else {
						LOGGER.info("NO COLORS TO AVERAGE");
						cur.setColorHex("000000");
						save.add(cur);
						currencyRepo.save(cur);
					}
					
				} catch (Exception e) {
					LOGGER.error("",e);
				} 
					
				
					
				//}
				
			});
			
		});
			
			LOGGER.info("Done with all colors");

	}
	
	protected void updateRoles() {
		
		template.executeWithoutResult(action -> {
		
			Role user = new Role("User");
			user.updatePermissions(
					Permission.of(Feature.Portfolio, Access.ReadWriteDelete),
					Permission.of(Feature.Currency, Access.Read),
					Permission.of(Feature.Exchange, Access.Read)
			);
			updateRole(user);
			
			Role sup = new Role("Superuser");
			sup.updatePermissions(
					Permission.of(Feature.System, Access.ReadWrite)
			);
			updateRole(sup);
			
			Role admin = new Role("Administrator");
			admin.updateAllPermissions(Access.Super);
			updateRole(admin);
			
		
		});
	}
	
	protected void updateRole(Role r) {
		
		try {
			LOGGER.info("updateRole: {}",r);
			Optional<Role> role = roleRepo.findByKey(r.getKey());
			LOGGER.info("found: {}",role);
			if(role.isPresent()) role.get().update(r);
			else {
				LOGGER.info("Saving role: {}",r);
				roleRepo.save(r);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void updateFiats() {

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

	protected void updateCurrency(Coin coin) {
		
		LOGGER.info("updateCurrency coin: {}",coin);

		template.executeWithoutResult(action -> {

			Currency currency = currMap.get(coin.getId());
			if(currency == null) currency = new Currency(null, null, coin.getId());
			currency.setName(coin.getName());
			currency.setKey(coin.getSymbol());
			
			LOGGER.info("Coin: {}",coin);
			LOGGER.info("supply: {}",coin.getCirculatingSupply());
			LOGGER.info("rating: {}",coin.getRating());
					
			
			if((coin.getCirculatingSupply()!= null && coin.getCirculatingSupply().compareTo(BigDecimal.ZERO) > 0) || coin.hasRating()) {
				currency.setActive(true);
			}
			else {
				currency.setActive(false);
			}

			if(coin.getBuiltOn()!=null) {
				Currency b = currencyRepo.findByKey(coin.getBuiltOn());
				currency.setBuiltOn(b);
			}
			currency.setPlatformType(coin.getPlatformType()==null?null:PlatformType.valueOf(coin.getPlatformType()));


			try {
				imageService.syncImage(currency, coin.getImageUrl());

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

			currencyRepo.save(currency);

		});

	}

	protected void syncCurrencies() {
		
		LOGGER.info("updateCurrencies");

		currMap = currencyRepo.findByRemoteIdNotNull().stream().collect(Collectors.toMap(Currency::getRemoteId,Function.identity()));

		Collection<Coin> coins = compare.getCoins();
		LOGGER.info("coins: {}",coins.size());
		coins.forEach(coin -> {
			updateCurrency(coin);
		});


	}
	
	protected void syncGrades() {
		
		LOGGER.info("updateGrades");
		
		ExchangesCurrencyPairs response = compare.getExchangeCurrencyPairs(false);
		Map<String,ExchangePairs> map = response.getExchangeNamePairsMap();
		
		//LOGGER.info("map: {}",map.size());
			
		List<Currency> currencies = currencyRepo.findAll();
		Map<String,Currency> keyMap = currencies.stream().collect(Collectors.toMap(c -> {
			return c.getKey().toLowerCase();
		}, Function.identity()));

		map.forEach((name,pairs) -> {
			
			template.executeWithoutResult(action -> {
			
				//LOGGER.info("exchange: {}",name);
				Exchange e = exchangeRepo.findByRemoteName(name);
					
				if(e != null && e.getGrade() != null) {
					//LOGGER.info("update pairs for: {} with grade: {}",e,e.getGrade());
					pairs.getPairsMap().forEach((currencyKey,otherPairs) -> {
						//LOGGER.info("currencyKey: {}",currencyKey);
						//Currency curr = currencyRepo.findByKeyIgnoreCase(currencyKey);
						Currency curr = keyMap.get(currencyKey.toLowerCase());
						//LOGGER.info("curr: {}",curr);
						if(curr != null && (curr.getGrade() == null || curr.getGrade().compareTo(e.getGrade())>0 )) {
							//LOGGER.info("Update grade for {} to {}",curr,e.getGrade());
							curr.setGrade(e.getGrade());
							currencyRepo.save(curr);
						}
						
					});
				}
				//}
				
				
				
			});
			

		});
		/*currMap = currencyRepo.findByRemoteIdNotNull().stream().collect(Collectors.toMap(Currency::getRemoteId,Function.identity()));
		
		Collection<Coin> coins = compare.getCoins();
		LOGGER.info("coins: {}",coins.size());
		coins.forEach(coin -> {
			updateCurrency(coin);
		});*/


	}

	protected void resetDevPasswords() {
		
		LOGGER.info("resetDevPasswords");

		template.executeWithoutResult(action -> {

			userRepo.findAll().forEach(user -> {
				if(user.getPassword().equals("dev")) {
					userService.setPassword(user, "dev");
				}
			});

		});

	}

	protected void syncExchanges() {
		
		LOGGER.info("updateExchanges");

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
					e = new Exchange(ex.getName(), ex.getId(), ex.getInternalName(),null,null);
					LOGGER.info("Creating Exchange: {}",e);

				}
				else {
					e = idMap.get(ex.getId());
				}
				e.setGrade(Grade.from(ex.getGrade()));
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
