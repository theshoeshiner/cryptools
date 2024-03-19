package org.thshsh.crypt;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.crypt.web.view.manage.PortfolioEntryGrid;

@Entity()
@Table(schema = CryptmanModel.SCHEMA, name = "entry_history",indexes = {
		@Index(columnList = "portfolio_id")
})
public class PortfolioEntryHistory extends IdedEntity {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioEntryHistory.class);


	@ManyToOne()
	protected Currency currency;

	@Column(columnDefinition = "decimal")
	protected BigDecimal balance;

	@Column(columnDefinition = "decimal")
	protected BigDecimal value;

	@ManyToOne
	protected PortfolioHistory portfolio;

	@Column(columnDefinition = "decimal")
	protected BigDecimal thresholdPercent;

	@Column(columnDefinition = "decimal")
	protected BigDecimal toTriggerPercent;

	@Column(columnDefinition = "decimal")
	protected BigDecimal adjustPercent;
	
	
	@ManyToOne()
	protected MarketRate rate;
	@Column(columnDefinition = "decimal")
	protected BigDecimal targetReserve;
	@Column(columnDefinition = "decimal")
	protected BigDecimal adjustReserve;
	@Column(columnDefinition = "decimal")
	protected BigDecimal adjust;
	@Column(columnDefinition = "decimal")
	protected BigDecimal adjustAbsolute;
	@Column(columnDefinition = "decimal")
	protected BigDecimal adjustPercentAbsolute;
	
	@Column(columnDefinition = "decimal")
	protected BigDecimal allocationPercent;

	@Column
	protected Boolean allocationUndefined;


	public PortfolioEntryHistory() {}

	/*public PortfolioEntryHistory(PortfolioHistory p, PortfolioEntry pe) {
		this.currency = pe.getCurrency();
		this.balance = pe.getBalance();
		this.value = pe.getValueReserve();
		this.portfolio = p;
		this.thresholdPercent = pe.getThresholdPercent();
		this.toTriggerPercent = pe.getToTriggerPercentOrZero();
		this.adjustPercent = pe.getAdjustPercent();
	}*/
	
	public PortfolioEntryHistory(PortfolioHistory portfolio,BigDecimal balance, Currency currency,Allocation a,MarketRate rate) {
		super();
		this.portfolio = portfolio;
		this.balance = balance;
		this.currency = currency;
		//this.allocation = a;
		if(a!=null) {
			this.allocationPercent = a.getPercent();
			this.allocationUndefined = a.getUndefined();
		}
		if(this.allocationUndefined == null) this.allocationUndefined = this.allocationPercent == null; 
		//else this.allocationUndefined = true;
		this.rate = rate;
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



	public PortfolioHistory getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(PortfolioHistory portfolio) {
		this.portfolio = portfolio;
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
		this.setAdjustReserve(this.targetReserve.subtract(this.value));
		LOGGER.info("rate: {}",rate);
		
		if(rate == null) {
			this.adjust = this.adjustReserve;
		}
		else if(rate.getRate().compareTo(BigDecimal.ZERO) == 0) {
			this.adjust = null;
		}
		else {
			this.adjust = this.adjustReserve.divide(rate.getRate(),RoundingMode.HALF_EVEN);
		}
		
	}

	public BigDecimal getValueReserve() {
		return value;
	}

	/*public void setValueReserve(BigDecimal value) {
		this.value = value;
	}*/
	
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
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
	
	public String getToTriggerPercentOrZeroString() {
		BigDecimal p = getToTriggerPercentOrZero();
		return PortfolioEntryGrid.PercentFormat.format(p);
	}

	public BigDecimal getToTriggerPercent() {
		return toTriggerPercent;
	}

	public void setToTriggerPercent(BigDecimal toTriggerPercent) {
		this.toTriggerPercent = toTriggerPercent.abs();
	}
	
	

	public void setAllocationPercent(BigDecimal allocationPercent) {
		this.allocationPercent = allocationPercent;
	}

	public void setAllocationUndefined(Boolean allocationUndefined) {
		this.allocationUndefined = allocationUndefined;
	}

	public BigDecimal getAllocationPercent() {
		return allocationPercent;
	}

	public Boolean getAllocationUndefined() {
		return allocationUndefined;
	}

	@Override
	public String toString() {
		return "[currency=" + currency + ", balance=" + balance + ", value=" + value
				+ ", allocationPercent=" + allocationPercent + ", allocationUndefined=" + allocationUndefined + "]";
	}



}
