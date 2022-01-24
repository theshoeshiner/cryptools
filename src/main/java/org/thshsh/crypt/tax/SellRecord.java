package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SellRecord extends GainRecord {



	List<Sale> saleRecords;

	//aggregation of buy records that we are selling from
	SaleAggregate shortTermAggregate;
	SaleAggregate longTermAggregate;

	SellRecord(BigDecimal quan, BigDecimal pri,Asset a,Transaction transaction) {
		super(a,quan,pri,Transaction.Type.Sell,transaction);
		this.balance = quan;
		this.saleRecords = new ArrayList<>();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SellRecord [id=");
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
		builder.append(", transaction=");
		builder.append(transaction);
		/*builder.append(", shortTermAggregate=");
		builder.append(shortTermAggregate);
		builder.append(", longTermAggregate=");
		builder.append(longTermAggregate);*/
		builder.append("]");
		return builder.toString();
	}

	public List<Sale> getSaleRecords() {
		return saleRecords;
	}
	
	

}
