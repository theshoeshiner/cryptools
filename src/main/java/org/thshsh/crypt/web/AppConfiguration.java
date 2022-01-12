package org.thshsh.crypt.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
public class AppConfiguration {

	//@Value("${app.productionMode}")
	Boolean productionMode = false;

	CryptoCompareConfiguration cryptocompare;

	//@Value("${app.login.enabled}")
	Boolean login = true;

	//@Value("${app.login.username:}")
	String username;
	
	//@Value("${app.historyjob.enabled:true}")
	//Boolean historyJobEnabled;
	
	//@Value("${app.apikey.required:true}")
	Boolean requireApiKey = true;
	
	JobConfiguration job;
	
	String url;
	
	MediaConfiguration media;
	

	public Boolean getProductionMode() {
		return productionMode;
	}

	public void setProductionMode(Boolean productionMode) {
		this.productionMode = productionMode;
	}



	public CryptoCompareConfiguration getCryptocompare() {
		return cryptocompare;
	}

	public void setCryptocompare(CryptoCompareConfiguration cryptocompare) {
		this.cryptocompare = cryptocompare;
	}

	public Boolean getLogin() {
		return login;
	}

	public void setLogin(Boolean login) {
		this.login = login;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public JobConfiguration getJob() {
		return job;
	}

	public void setJob(JobConfiguration job) {
		this.job = job;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MediaConfiguration getMedia() {
		return media;
	}

	public void setMedia(MediaConfiguration media) {
		this.media = media;
	}

	public void setRequireApiKey(Boolean requireApiKey) {
		this.requireApiKey = requireApiKey;
	}

	public Boolean getRequireApiKey() {
		return requireApiKey;
	}
	
	

	/*	public Boolean getCryptoCompareSync() {
			return cryptoCompareSync;
		}
	
		public void setCryptoCompareSync(Boolean cryptoCompareSync) {
			this.cryptoCompareSync = cryptoCompareSync;
		}*/

	

}
