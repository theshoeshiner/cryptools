package org.thshsh.crypt;

import java.math.BigDecimal;
import java.util.List;


public class PortfolioSummary {

	List<PortfolioEntryHistory> entries;
	BigDecimal totalValue;
	BigDecimal maxToTriggerPercent;
	BigDecimal totalAdjustPercent;


	public List<PortfolioEntryHistory> getEntries() {
		return entries;
	}
	public void setEntries(List<PortfolioEntryHistory> entries) {
		this.entries = entries;
	}
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	public BigDecimal getMaxToTriggerPercent() {
		return maxToTriggerPercent;
	}
	public void setMaxToTriggerPercent(BigDecimal maxToTrigger) {
		this.maxToTriggerPercent = maxToTrigger;
	}
	public BigDecimal getTotalAdjustPercent() {
		return totalAdjustPercent;
	}
	public void setTotalAdjustPercent(BigDecimal totalAdjust) {
		this.totalAdjustPercent = totalAdjust;
	}



}