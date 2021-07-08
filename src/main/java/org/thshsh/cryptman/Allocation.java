package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.crypt.Currency;
import org.thshsh.crypt.IdedEntity;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "allocation")
public class Allocation extends IdedEntity {

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
		return "[portfolio=" + portfolio + ", currency=" + currency + ", percent=" + percent + "]";
	}

	/*public static String getPercentString(BigDecimal bd) {
		return format.format(bd);
	}
	*/



}
