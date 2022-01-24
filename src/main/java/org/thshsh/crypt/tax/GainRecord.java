package org.thshsh.crypt.tax;

import java.math.BigDecimal;

/**
 * Either a sell or an income record
 * @author Dan
 *
 */
public abstract class GainRecord extends Record {

	Gains gains;
	
	public GainRecord(Asset a,BigDecimal q,BigDecimal pri,Transaction.Type tp,Transaction t) {
		super(a,q,pri,tp,t);
	}


}
