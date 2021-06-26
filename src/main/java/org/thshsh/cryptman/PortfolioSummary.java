package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.util.List;

import org.thshsh.crypt.web.view.PortfolioEntry;

public class PortfolioSummary {

	List<PortfolioEntry> entries;
	BigDecimal totalValue;
	BigDecimal maxToTriggerPercent;
	BigDecimal totalAdjustPercent;


	public List<PortfolioEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<PortfolioEntry> entries) {
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