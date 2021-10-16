package org.thshsh.crypt;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "allocation",indexes = {
		@Index(columnList = "portfolio_id")
})
public class Allocation extends IdedEntity {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Allocation.class);

	//protected static MathContext CONTEXT = new MathContext(4);

	//static NumberFormat format = new DecimalFormat("##.#'%'");

	@ManyToOne(optional = false)
	Currency currency;

	@Column(columnDefinition = "decimal")
	BigDecimal percent;

	@ManyToOne
	Portfolio portfolio;

	Boolean undefined;

	public Allocation() {}

	public Allocation(BigDecimal percent) {
		super();
		this.percent = percent;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setUndefined(Boolean t) {
		this.undefined = t;
	}

	public Boolean isUndefined() {
		LOGGER.info("isUndefined: {}",this);
		if(undefined != null) return undefined;
		else return percent == null;
	}

	/*public String getPercentString() {
		return getPercentString(this.percent);
	}*/

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}


	@Override
	public String toString() {
		return "[id=" + id + ", currency=" + currency + ", percent=" + percent + "]";
	}

	/*public static String getPercentString(BigDecimal bd) {
		return format.format(bd);
	}
	*/



}
