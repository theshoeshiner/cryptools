package org.thshsh.crypt;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Embeddable
public class PortfolioSettings {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioSettings.class);

	@ManyToOne()
	Currency reserve;

	@Column(columnDefinition = "decimal")
	BigDecimal minimumAdjust;
	
	@Column()
	Boolean alertsDisabled;
	
	@Column
	ZonedDateTime silentTill;
	
	@Column(columnDefinition = "decimal")
	BigDecimal individualThreshold;
	
	@Column(columnDefinition = "decimal")
	BigDecimal portfolioThreshold;

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

	public BigDecimal getIndividualThreshold() {
		return individualThreshold;
	}

	public void setIndividualThreshold(BigDecimal individualThreshold) {
		this.individualThreshold = individualThreshold;
	}

	public BigDecimal getPortfolioThreshold() {
		return portfolioThreshold;
	}

	public void setPortfolioThreshold(BigDecimal portfolioThreshold) {
		this.portfolioThreshold = portfolioThreshold;
	}

	public Duration getSilentTillDuration() {
		LOGGER.info("getSilentTillDuration: {}",silentTill);
		if(silentTill==null) return null;
		else return Duration.between(ZonedDateTime.now(), silentTill);
	}
	
	public void setSilentTillDuration(Duration d) {
		if(d == null) silentTill = null;
		else silentTill  = ZonedDateTime.from(d.addTo(ZonedDateTime.now()));
	}

}
