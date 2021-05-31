package org.thshsh.crypt.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.thshsh.crypt.cryptocompare.CryptoCompare;

@Configuration
public class Beans {

	@Value("${cryptocompare.apikey}")
	String apiKey;

	@Bean
	@Scope("prototype")
	public CryptoCompare cryptoCompare() {
		return new CryptoCompare(apiKey,null);
	}

}
