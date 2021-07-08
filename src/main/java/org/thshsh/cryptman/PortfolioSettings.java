package org.thshsh.cryptman;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.thshsh.crypt.Currency;

@Embeddable
public class PortfolioSettings {

	@ManyToOne()
	Currency reserve;

	@Column(columnDefinition = "decimal")
	BigDecimal minimumAdjust;

	public Currency getReserve() {
		return reserve;
	}

	public void setReserve(Currency reserve) {
		this.reserve = reserve;
	}

	public BigDecimal getMinimumAdjust() {
		return minimumAdjust;
	}

	public void setMinimumAdjust(BigDecimal alertMinimumThreshold) {
		this.minimumAdjust = alertMinimumThreshold;
	}



}