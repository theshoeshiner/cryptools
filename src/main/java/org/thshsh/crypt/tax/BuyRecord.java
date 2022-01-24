package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.crypt.tax.Transaction.Type;

public class BuyRecord extends Record {

	public static final Logger LOGGER = LoggerFactory.getLogger(BuyRecord.class);

	BuyRecord(BigDecimal quan,BigDecimal pri,Transaction transaction, Asset a) {
		super(a,quan,pri,Transaction.Type.Buy,transaction);
		this.balance = quan;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BuyRecord [id=");
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
