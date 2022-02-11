package org.thshsh.crypt.serv;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.repo.CurrencyRepository;

@Service
public class CurrencyService {

	public static final Logger LOGGER = LoggerFactory.getLogger(CurrencyService.class);

	// File imagePath = new File("images");

	@Autowired
	CurrencyRepository currRepo;

	@Autowired
	CryptoCompare compare;

	@PostConstruct
	public void postConstruct() {

	}

	/*
	public Map<String, Currency> getOfficialKeyMap() {
		return getOfficialKeyMap(currRepo.findAll())
	}
	*/
	public Map<String, Currency> getOfficialKeyMap(Collection<Currency> currencies) {

		// Map<String,Currency> officialMap = new HashMap<String, Currency>();

		Map<String, Currency> officialMap = currencies.stream().filter(c -> c.getRemoteId() != null)
				.collect(Collectors.toMap(Currency::getKey, Function.identity()));
		
		return officialMap;

		/*currencies.stream().collect(Collectors.toMap(
				Currency::getKey, Function.identity(),(c0,c1) -> {
					LOGGER.error("There was a collision for symbol: {}",c0.getKey());
					
					//only one of them should have a remote id
					if(c1.getRemoteId() != null) return c1;
					else if(c0.getRemoteId() != null) return c1; 
					
					//Coin coin = compare.getCoin(c0.getKey(), true);
					//Currency match = collMap.get(c0.getKey()).stream().filter(c -> coin.getId().equals(c.getRemoteId())).findFirst().orElse(null);
					
					//if(coin == null) return null;
					
					if(!collMap.containsKey(c0.getKey())) collMap.put(c0.getKey(),new HashSet<>());
					collMap.get(c0.getKey()).add(c0);
					collMap.get(c0.getKey()).add(c1);
					
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
		Set<String> symbols = currencies.stream().map(c -> c.getKey()).collect(Collectors.toSet());*/

	}

	/*public InputStream getImage(Currency c) throws IOException {
		if(c.getImageUrl()!=null) {
			File image = new File(imagePath,c.getImageUrl());
			if(image.exists()) {
				return new FileInputStream(image);
			}
		}
		return null;
	}
	
	public InputStream getImage(String name) throws IOException {
		File image = new File(imagePath,name);
		if(image.exists()) return new FileInputStream(image);
		else return null;
	}
	
	public void saveImage(Currency c,InputStream is,String name) throws IOException {
		LOGGER.info("Saving image: {}",name);
	
		File image = new File(imagePath,name);
		FileOutputStream fos = new FileOutputStream(image);
		IOUtils.copy(is, fos);
		c.setImageUrl(name);
	}*/

}
