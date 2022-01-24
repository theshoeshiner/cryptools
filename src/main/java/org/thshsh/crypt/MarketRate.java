package org.thshsh.crypt;

import java.math.BigDecimal;
import java.time.Duration;
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

	@Column(columnDefinition = "decimal")
	BigDecimal rate;
	
	@Column(columnDefinition = "decimal")
	public BigDecimal high;
	
	@Column(columnDefinition = "decimal")
	public BigDecimal low;
	
	@Column(columnDefinition = "decimal")
	public BigDecimal open;
	
	@Column(columnDefinition = "decimal")
	public BigDecimal close;

	@Column
	ZonedDateTime timestamp;
	
	@Column
	ZonedDateTime startTime;
	
	@Column
	ZonedDateTime endTime;



	public MarketRate() {}

	public MarketRate(Currency currency, BigDecimal high,BigDecimal low,BigDecimal open,BigDecimal close, ZonedDateTime start, ZonedDateTime end) {
		super();
		this.currency = currency;
		this.startTime = start;
		this.endTime = end;
		//timestamp is considered to be the exact middle
		this.timestamp = start.plus(Duration.between(start, end).dividedBy(2));
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.rate = low.add(high).add(open).add(close).divide(BigDecimal.valueOf(4));
	}
	
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

	/**
	 * This means the price is a historical quote from a specific hour instead of a realtime quote
	 */
	public Boolean isRange() {
		return startTime != null && endTime != null;
	}
	
	@Override
	public String toString() {
		return "[id=" + id + 
				", timestamp=" + timestamp + 
				", currency=" + currency + 
				", rate=" + rate +
				", high=" + high + ", low=" + low
				+ ", open=" + open + ", close=" + close + 
				", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}



}
