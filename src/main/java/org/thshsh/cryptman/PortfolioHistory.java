package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.thshsh.crypt.IdedEntity;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "portfolio_history")
public class PortfolioHistory extends IdedEntity {

	@ManyToOne
	Portfolio portfolio;

	@Column
	ZonedDateTime timestamp;

	//@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	//Set<BalanceHistory> balances;

	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	Set<PortfolioEntryHistory> entries;

	@Column(columnDefinition = "decimal")
	BigDecimal value;

	@Column(columnDefinition = "decimal")
	BigDecimal maxToTriggerPercent;

	@Column(columnDefinition = "decimal")
	BigDecimal totalImbalance;

	public PortfolioHistory() {}

	public PortfolioHistory(Portfolio portfolio,PortfolioSummary summary) {
		this.portfolio = portfolio;
		this.timestamp = ZonedDateTime.now();
		this.maxToTriggerPercent = summary.maxToTriggerPercent;
		this.value = summary.getTotalValue();
		this.totalImbalance = BigDecimal.ZERO;
	}


	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}



	public Set<PortfolioEntryHistory> getEntries() {
		if(entries == null) entries = new HashSet<PortfolioEntryHistory>();
		return entries;
	}

	public void addEntry(PortfolioEntryHistory e) {
		getEntries().add(e);
		this.totalImbalance = this.totalImbalance.add(e.getAdjustPercent().abs());
	}

	public void setEntries(Set<PortfolioEntryHistory> entries) {
		this.entries = entries;
	}

	/*public Set<BalanceHistory> getBalances() {
		if(balances == null) balances = new HashSet<BalanceHistory>();
		return balances;
	}

	public void setBalances(Set<BalanceHistory> balances) {
		this.balances = balances;
	}*/

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getMaxToTriggerPercent() {
		return maxToTriggerPercent;
	}

	public void setMaxToTriggerPercent(BigDecimal maxToTriggerPercent) {
		this.maxToTriggerPercent = maxToTriggerPercent;
	}



	public BigDecimal getTotalImbalance() {
		return totalImbalance;
	}

	public void setTotalImbalance(BigDecimal totalImbalance) {
		this.totalImbalance = totalImbalance;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", timestamp=" + timestamp + ", value=" + value + ", maxToTriggerPercent="
				+ maxToTriggerPercent + "]";
	}




}
