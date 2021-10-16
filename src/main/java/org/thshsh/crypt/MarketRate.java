package org.thshsh.crypt;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "market_rate")
public class MarketRate extends IdedEntity {

	@ManyToOne
	Currency currency;

	@Column
	BigDecimal rate;

	@Column
	ZonedDateTime timestamp;



	public MarketRate() {}

	public MarketRate(Currency currency, BigDecimal rate, ZonedDateTime timestamp) {
		super();
		this.currency = currency;
		this.rate = rate;
		this.timestamp = timestamp;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getRate() {
		return rate;
	}



	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", currency=" + currency + ", rate=" + rate + ", timestamp=" + timestamp + "]";
	}



}
