package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.thshsh.crypt.Currency;
import org.thshsh.cryptman.Allocation;
import org.thshsh.cryptman.MarketRate;
import org.thshsh.cryptman.Portfolio;

public class PortfolioEntry {

	BigDecimal balance;

	Portfolio portfolio;
	Currency currency;
	Allocation allocation;
	MarketRate rate;

	BigDecimal targetReserve;
	BigDecimal valueReserve;
	BigDecimal adjustReserve;


	BigDecimal adjust;

	BigDecimal adjustAbsolute;

	BigDecimal adjustPercent;
	BigDecimal adjustPercentAbsolute;

	BigDecimal thresholdPercent;
	BigDecimal toTriggerPercent;

	//static NumberFormat format = new DecimalFormat("$#,##0.00");

	public PortfolioEntry() {}

	public PortfolioEntry(Portfolio portfolio,BigDecimal balance, Currency currency,Allocation a,MarketRate rate) {
		super();
		this.portfolio = portfolio;
		this.balance = balance;
		this.currency = currency;
		this.allocation = a;
		this.rate = rate;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Allocation getAllocation() {
		return allocation;
	}

	public void setAllocation(Allocation allocation) {
		this.allocation = allocation;
	}

	public MarketRate getRate() {
		return rate;
	}

	public void setRate(MarketRate rate) {
		this.rate = rate;
	}

	public BigDecimal getTargetReserve() {
		return targetReserve;
	}

	public void setTargetReserve(BigDecimal target) {
		this.targetReserve = target;
		this.setAdjustReserve(this.targetReserve.subtract(this.valueReserve));
		this.adjust = this.adjustReserve.divide(rate.getRate(),RoundingMode.HALF_EVEN);
	}

	public BigDecimal getValueReserve() {
		return valueReserve;
	}



	/*public String getValueString() {
		return format.format(value);
	}

	public String getTargetString() {
		if(target != null) return format.format(target);
		else return format.format(BigDecimal.ZERO);
	}*/

	public BigDecimal getAdjustReserve() {
		return adjustReserve;
	}

	public BigDecimal getAdjustAbsolute() {
		return adjustAbsolute;
	}

	public void setAdjustReserve(BigDecimal adjust) {
		this.adjustReserve = adjust;
		this.adjustAbsolute = adjust.abs();
	}

	public void setValueReserve(BigDecimal value) {
		this.valueReserve = value;
	}

	public BigDecimal getAdjustPercent() {
		return adjustPercent;
	}

	public void setAdjustPercent(BigDecimal adjustPercent) {
		this.adjustPercent = adjustPercent;
		this.adjustPercentAbsolute = adjustPercent.abs();
	}

	public void setAdjustAbsolute(BigDecimal adjustAbsolute) {
		this.adjustAbsolute = adjustAbsolute;
	}

	public BigDecimal getAdjustPercentAbsolute() {
		return adjustPercentAbsolute;
	}

	public BigDecimal getAdjust() {
		return adjust;
	}

	public BigDecimal getThresholdPercent() {
		return thresholdPercent;
	}

	public void setThresholdPercent(BigDecimal thresholdPercent) {
		this.thresholdPercent = thresholdPercent;
	}

	public BigDecimal getToTriggerPercentOrZero() {
		if(toTriggerPercent == null) return BigDecimal.ZERO;
		else return toTriggerPercent;
	}

	public BigDecimal getToTriggerPercent() {
		return toTriggerPercent;
	}

	public void setToTriggerPercent(BigDecimal toTriggerPercent) {
		this.toTriggerPercent = toTriggerPercent.abs();
	}





}