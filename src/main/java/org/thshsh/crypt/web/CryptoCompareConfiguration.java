package org.thshsh.crypt.web;

import org.springframework.beans.factory.annotation.Value;

public class CryptoCompareConfiguration {



	//@Value("${app.cryptocompare.sync.exchanges}")
	Boolean exchanges;
	
	//@Value("${app.cryptocompare.sync.coins}")
	Boolean coins;
	
	//@Value("${app.cryptocompare.sync.images}")
	Boolean images;
	
	//@Value("${app.cryptocompare.sync.grades}")
	Boolean grades;

	public Boolean getExchanges() {
		return exchanges;
	}

	public void setExchanges(Boolean exchanges) {
		this.exchanges = exchanges;
	}

	public Boolean getCoins() {
		return coins;
	}

	public void setCoins(Boolean coins) {
		this.coins = coins;
	}

	public Boolean getImages() {
		return images;
	}

	public void setImages(Boolean images) {
		this.images = images;
	}

	public Boolean getGrades() {
		return grades;
	}

	public void setGrades(Boolean grades) {
		this.grades = grades;
	}
	
	
	
}
