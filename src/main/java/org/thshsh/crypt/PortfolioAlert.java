package org.thshsh.crypt;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity()
@Table(schema = CryptmanModel.SCHEMA, name = "alert",indexes = {
		@Index(columnList = "portfolio_id")
})
public class PortfolioAlert extends IdedEntity { 
	
	@ManyToOne
	Portfolio portfolio;
	
	@ManyToOne
	PortfolioHistory history;
	
	@Column
	ZonedDateTime timestamp;
	
	@Column
	Integer repeat;
	
	@Column
	Boolean muted;

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public PortfolioHistory getHistory() {
		return history;
	}

	public void setHistory(PortfolioHistory history) {
		this.history = history;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getRepeat() {
		return repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	public Boolean getMuted() {
		return muted;
	}

	public void setMuted(Boolean muted) {
		this.muted = muted;
	}
	
	

}
