package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

/**
 * Represents a transaction, but holds references to assets and other objects
 * @author Dan
 *
 */
public abstract class Record {

	//transaction and derived fields
	Transaction transaction;
	ZonedDateTime timestamp;
	String id;
	
	Transaction.Type type;
	BigDecimal quantity;
	protected Asset asset;
	
	//These are in fiat USD
	BigDecimal price;
	BigDecimal pricePer;
	
	//this is used to track the running balance for this lot
	BigDecimal balance;
	
	public Record(Asset a,BigDecimal q,BigDecimal pri,Transaction.Type type,Transaction t) {
		this.transaction = t;
		this.id = t.id;
		this.timestamp = t.timestamp;
		this.asset = a;
		this.type = type;
		this.quantity = q;
		
		this.price = pri;
		this.pricePer = this.price.divide(quantity,RoundingMode.HALF_EVEN);
		
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public String getId() {
		return id;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public Asset getAsset() {
		return asset;
	}

	public BigDecimal getBalance() {
		return balance;
	}
	
	public Boolean hasBalance() {
		return balance != null;
	}

	public Transaction.Type getType() {
		return type;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getPricePer() {
		return pricePer;
	}
	
	
}
