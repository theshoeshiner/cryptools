package org.thshsh.crypt.tax;

import java.math.BigDecimal;

/** 
 * Represents a specific sell from a lot. Multiple of these can be created by any given SELL transaction.
 * @author Dan
 *
 */
public class Sale {

	String id;

	BigDecimal basis;
	BigDecimal proceeds;
	BigDecimal gain;
	BigDecimal quantity;
	Record buyRecord;
	SellRecord sellRecord;
	Boolean shortTerm;

	Sale(BigDecimal basis, BigDecimal proceeds, BigDecimal quantity,Record buyRecord,SellRecord sellRecord) {
		this.basis = basis;
		this.proceeds = proceeds;
		this.gain = proceeds.subtract(basis);
		this.quantity = quantity;
		this.buyRecord = buyRecord;
		this.sellRecord = sellRecord;
	}

	

	public String getId() {
		return id;
	}



	public BigDecimal getBasis() {
		return basis;
	}



	public BigDecimal getProceeds() {
		return proceeds;
	}



	public BigDecimal getGain() {
		return gain;
	}



	public BigDecimal getQuantity() {
		return quantity;
	}



	public Record getBuyRecord() {
		return buyRecord;
	}



	public SellRecord getSellRecord() {
		return sellRecord;
	}



	public Boolean getShortTerm() {
		return shortTerm;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[id=");
		builder.append(id);
		builder.append(", basis=");
		builder.append(basis);
		builder.append(", proceeds=");
		builder.append(proceeds);
		builder.append(", gain=");
		builder.append(gain);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", buyRecord=");
		builder.append(buyRecord);
		builder.append(", sellRecord=");
		builder.append(sellRecord);
		builder.append(", shortTerm=");
		builder.append(shortTerm);
		builder.append("]");
		return builder.toString();
	}

	
}
