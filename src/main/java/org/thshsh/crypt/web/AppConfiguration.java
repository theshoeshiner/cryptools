package org.thshsh.crypt.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class AppConfiguration {

	@Value("${app.productionMode}")
	Boolean productionMode;

	@Value("${app.cryptocompare.sync}")
	Boolean cryptoCompareSync;
	
	@Value("${app.cryptocompare.sync.exchanges}")
	Boolean syncExchanges;
	
	@Value("${app.cryptocompare.sync.coins}")
	Boolean syncCoins;
	
	@Value("${app.cryptocompare.sync.images}")
	Boolean syncImages;
	
	@Value("${app.cryptocompare.sync.grades}")
	Boolean syncGrades;

	@Value("${app.login.enabled}")
	Boolean loginEnabled;

	@Value("${app.login.username:}")
	String username;

	public Boolean getProductionMode() {
		return productionMode;
	}

	public void setProductionMode(Boolean productionMode) {
		this.productionMode = productionMode;
	}

	public Boolean getLoginEnabled() {
		return loginEnabled;
	}

	/*	public Boolean getCryptoCompareSync() {
			return cryptoCompareSync;
		}
	
		public void setCryptoCompareSync(Boolean cryptoCompareSync) {
			this.cryptoCompareSync = cryptoCompareSync;
		}*/



}
