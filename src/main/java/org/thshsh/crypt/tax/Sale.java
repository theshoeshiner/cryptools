package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Sale {

	String id;

	BigDecimal basis;
	BigDecimal proceeds;
	BigDecimal gain;
	BigDecimal quantity;
	BuyRecord buyRecord;
	SellRecord sellRecord;

	List<BuyRecord> buyRecords;
	Boolean shortTerm;

	Sale(BigDecimal basis, BigDecimal proceeds, BigDecimal quantity,BuyRecord buyRecord,SellRecord sellRecord) {
		/*
		this.quantity = quan;
		this.timestamp = transaction.time;
		this.price = pri;
		this.pricePer = this.price.divide(quan);
		this.balance = quan;
		this.saleRecords = new Array();
		*/
		this.basis = basis;
		this.proceeds = proceeds;
		this.gain = proceeds.subtract(basis);
		this.quantity = quantity;
		this.buyRecord = buyRecord;
		this.sellRecord = sellRecord;

		this.buyRecords = new ArrayList<>();
	}

	void aggregate(Sale sale) {
		this.basis = this.basis.add(sale.basis);
		this.proceeds = this.proceeds.add(sale.proceeds);
		this.quantity = this.quantity.add(sale.quantity);
		this.gain = this.proceeds.subtract(this.basis);
		this.buyRecords.add(sale.buyRecord);

		if(this.buyRecord == null ||
				//this.buyRecord.timestamp.getTime() < sale.buyRecord.timestamp.getTime()
				this.buyRecord.timestamp.compareTo(sale.buyRecord.timestamp) < 0
				) this.buyRecord = sale.buyRecord;

	}

}
