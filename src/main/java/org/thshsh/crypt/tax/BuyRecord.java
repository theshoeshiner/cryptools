package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuyRecord extends Record {

	public static final Logger LOGGER = LoggerFactory.getLogger(BuyRecord.class);

	String id;
	BigDecimal quantity;
	BigDecimal price;
	BigDecimal pricePer;
	BigDecimal balance;
	LocalDateTime timestamp;
	Asset asset;


	BuyRecord(BigDecimal quan,BigDecimal pri,Transaction transaction) {
		this.id = transaction.id;
		this.transaction = transaction;
		this.quantity = quan;
		this.timestamp = transaction.timestamp;
		this.price = pri;
		this.pricePer = this.price.divide(quan,RoundingMode.HALF_EVEN);

		//used to track how many we have left from this lot
		this.balance = quan;
		//this.washedLosses = new BigNum(0);

		//this.LOGGER = LoggerFactory.getLogger("BuyRecord");

	}



}
