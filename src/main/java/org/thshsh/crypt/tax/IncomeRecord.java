package org.thshsh.crypt.tax;

import java.math.BigDecimal;

public class IncomeRecord extends GainRecord {


	IncomeRecord(Asset a,BigDecimal q,BigDecimal p,Transaction transaction) {
		super(a,q,p,Transaction.Type.Income,transaction);
		this.balance = q;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IncomeRecord [id=");
		builder.append(id);
		builder.append(", asset=");
		builder.append(asset);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", price=");
		builder.append(price);
		builder.append(", pricePer=");
		builder.append(pricePer);
		builder.append(", balance=");
		builder.append(balance);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}

	
	
}
