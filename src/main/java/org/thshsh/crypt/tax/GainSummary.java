package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an aggregation of sales
 * @author Dan
 *
 */
public class GainSummary {

	BigDecimal basis;
	BigDecimal proceeds;
	BigDecimal quantity;
	BigDecimal gains;
	List<Sale> saleRecords;

	Record buyRecord;

	GainSummary(){

		this.basis = BigDecimal.ZERO;
		this.proceeds =  BigDecimal.ZERO;
		this.quantity =  BigDecimal.ZERO;
		this.saleRecords = new ArrayList<>();
	}

	public void aggregate(Sale sale) {

		this.saleRecords.add(sale);
		this.basis = this.basis.add(sale.basis);
		this.proceeds = this.proceeds.add(sale.proceeds);
		this.quantity = this.quantity.add(sale.quantity);
		this.gains = this.proceeds.subtract(this.basis);
		//this.buyRecords.push(sale.buyRecord);
		if(this.buyRecord == null || this.buyRecord.timestamp.compareTo(sale.buyRecord.timestamp) < 0) this.buyRecord = sale.buyRecord;

	}

}
