package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A single SELL transaction will actually create multiple sells because it will sell assets from different lots. These
 * multiple sells can be aggregated together for reporting, as long as the cost basis and proceeds are reported correctly.
 * This class is used to aggregate those together so they can be reported as a single transaction.
 * @author Dan
 *
 */
public class SaleAggregate extends Sale {
	
	List<Sale> sales;
	List<Record> buyRecords;

	SaleAggregate(String id,SellRecord sr,List<Sale> sales) {
		super(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null,sr);
		this.buyRecords = new ArrayList<>();
		this.sales = new ArrayList<Sale>(sales);
		this.id = id;
		this.sellRecord = sr;
		sales.forEach(s -> aggregate(s));
	}
	

	/*void aggregate(Collection<Sale> sales) {
		sales.forEach(s -> aggregate(s));
	}*/
	
	private void aggregate(Sale sale) {
		this.basis = this.basis.add(sale.basis);
		this.proceeds = this.proceeds.add(sale.proceeds);
		this.quantity = this.quantity.add(sale.quantity);
		this.gain = this.proceeds.subtract(this.basis);
		this.buyRecords.add(sale.buyRecord);

		if(this.buyRecord == null || this.buyRecord.timestamp.isBefore(sale.buyRecord.timestamp) ) {
			//only keep track of the newest buy record when aggregating sales, since that represents the entire batch in terms of long vs short
			this.buyRecord = sale.buyRecord;
		}
		
		

	}
	
}
