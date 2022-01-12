package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SellRecord extends GainRecord {

	Asset asset;
	String id;
	BigDecimal quantity;
	BigDecimal price;
	BigDecimal pricePer;
	BigDecimal balance;

	LocalDateTime timestamp;

	List<Sale> saleRecords;

	Sale shortTermAggregate;
	Sale longTermAggregate;

	SellRecord(BigDecimal quan, BigDecimal pri,Transaction transaction) {
		this.id = transaction.id;
		this.quantity = quan;
		this.transaction = transaction;
		this.timestamp = transaction.timestamp;
		this.price = pri;
		this.pricePer = this.price.divide(quan,RoundingMode.HALF_EVEN);
		this.balance = quan;
		this.saleRecords = new ArrayList<>();
	}

}
