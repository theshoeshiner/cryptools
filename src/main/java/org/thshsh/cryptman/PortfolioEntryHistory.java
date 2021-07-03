package org.thshsh.cryptman;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.crypt.Currency;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.crypt.web.view.PortfolioEntry;

@Entity()
@Table(schema = CryptmanModel.SCHEMA, name = "entry_history")
public class PortfolioEntryHistory extends IdedEntity {


	@ManyToOne(optional = false)
	Currency currency;

	@Column(columnDefinition = "decimal")
	BigDecimal balance;

	@Column(columnDefinition = "decimal")
	BigDecimal value;

	@ManyToOne
	PortfolioHistory portfolio;

	@Column(columnDefinition = "decimal")
	BigDecimal thresholdPercent;

	@Column(columnDefinition = "decimal")
	BigDecimal toTriggerPercent;

	@Column(columnDefinition = "decimal")
	BigDecimal adjustPercent;

	public PortfolioEntryHistory() {}

	public PortfolioEntryHistory(PortfolioHistory p, PortfolioEntry pe) {
		this.currency = pe.getCurrency();
		this.balance = pe.getBalance();
		this.value = pe.getValueReserve();
		this.portfolio = p;
		this.thresholdPercent = pe.getThresholdPercent();
		this.toTriggerPercent = pe.getToTriggerPercentOrZero();
		this.adjustPercent = pe.getAdjustPercent();
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public PortfolioHistory getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(PortfolioHistory portfolio) {
		this.portfolio = portfolio;
	}

	public BigDecimal getThresholdPercent() {
		return thresholdPercent;
	}

	public void setThresholdPercent(BigDecimal thresholdPercent) {
		this.thresholdPercent = thresholdPercent;
	}

	public BigDecimal getToTriggerPercent() {
		return toTriggerPercent;
	}

	public void setToTriggerPercent(BigDecimal toTriggerPercent) {
		this.toTriggerPercent = toTriggerPercent;
	}

	public BigDecimal getAdjustPercent() {
		return adjustPercent;
	}

	public void setAdjustPercent(BigDecimal adjustPercent) {
		this.adjustPercent = adjustPercent;
	}

	@Override
	public String toString() {
		return "[currency=" + currency + ", toTriggerPercent=" + toTriggerPercent + "]";
	}



}
