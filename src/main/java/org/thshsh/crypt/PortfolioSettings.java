package org.thshsh.crypt;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class PortfolioSettings {

	@ManyToOne()
	Currency reserve;

	@Column(columnDefinition = "decimal")
	BigDecimal minimumAdjust;
	
	@Column()
	Boolean alertsDisabled;
	
	@Column
	ZonedDateTime silentTill;

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

	public Boolean getAlertsDisabled() {
		return alertsDisabled;
	}
	
	public Boolean isAlertsDisabled() {
		return Boolean.TRUE.equals(alertsDisabled);
	}

	public void setAlertsDisabled(Boolean alertsDisabled) {
		this.alertsDisabled = alertsDisabled;
	}


	public ZonedDateTime getSilentTill() {
		return silentTill;
	}

	public void setSilentTill(ZonedDateTime silentTill) {
		this.silentTill = silentTill;
	}

	

}
