package org.thshsh.crypt.web;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crypt.data")
public class DataConfiguration {

	List<List<String>> fiatcurrencies;

	public List<List<String>> getFiatcurrencies() {
		return fiatcurrencies;
	}

	public void setFiatcurrencies(List<List<String>> currencies) {
		this.fiatcurrencies = currencies;
	}



}
