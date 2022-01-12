package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class Transaction {

	public enum Type {
		Buy,Sell,Deposit,Withdrawal,Income;
		//,Delist;
	}

	//raw fields from load
	public Type type;
	String id;
	String externalId;
	LocalDateTime timestamp;

	BigDecimal quantity;
	BigDecimal fee;
	BigDecimal remaining;
	String asset;
	String exchange;
	BigDecimal price;
	String[] market;

	BigDecimal quantityFrom;
	BigDecimal feeFrom;
	BigDecimal fiatFrom;
	BigDecimal fiatFeeFrom;
	BigDecimal fiatTo;
	BigDecimal pricePerFrom;
	BigDecimal fiatFeeTo;
	BigDecimal pricePerTo;

	String description;

	Boolean loaded = false;
	//Boolean delist = false;
	Boolean external = false;
	Boolean internal = false;
	Boolean phantom;
	ExchangeFile file;

	//post init values
	String assetFrom;
	String assetTo;
	BigDecimal quantityTo;
	BigDecimal feeTo;
	Long time;
	Transaction duplicate;

	Transfer transfer;

	//holds a reference to the exchange account
	Account account;

	public Transaction(String id) {
		this.externalId = id;
		//this.id = ((Integer)this.externalId.hashCode()).toString();
		this.id = Integer.toUnsignedString(this.externalId.hashCode());
	}

	public Long getEpochSecond() {
		return timestamp.toEpochSecond(ZoneOffset.UTC);
	}

	public void setTimestamp(LocalDateTime ts) {
		this.timestamp = ts;
		this.time = ts.toEpochSecond(ZoneOffset.UTC);
	}


	public Transaction(String id,LocalDateTime timestamp,Type type,BigDecimal quantity,String asset,String exchange,BigDecimal price,String[] market){

		this.externalId = id;
		//var hc = id.hashCode(); //
		//this.id = ((Integer)this.externalId.hashCode()).toU
		this.id = Integer.toUnsignedString(this.externalId.hashCode());
		//this.id = new BigNum(hc).unsigned32().toString().padStart(10,"0");

		//this.externalId = id;
		this.setTimestamp(timestamp);
		this.type = type;
		this.quantity = quantity;
		this.asset = asset;
		this.exchange=exchange;
		this.price = price;
		this.market = market;
	}


	/*public Transaction(String string, LocalDateTime ofEpochSecond, Type type2, BigDecimal quantity2, String asset2,
			Transaction exchange2) {
		// TODO Auto-generated constructor stub
	}*/

	public Transaction(String string, LocalDateTime ofEpochSecond, Type deposit, BigDecimal total, String asset2,String exchange2) {
		this.externalId = string;
		//this.id = ((Integer)this.externalId.hashCode()).toString();
		this.id = Integer.toUnsignedString(this.externalId.hashCode());
		this.timestamp = ofEpochSecond;
		this.type = deposit;
		this.quantity = total;
		this.asset = asset2;
		this.exchange =exchange2;
	}


	public Boolean isTransferType(){
		return this.type == Transaction.Type.Deposit || this.type == Transaction.Type.Withdrawal;
	}

	public Boolean isIncomeType(){
		return this.type == Transaction.Type.Income;
	}

	public Boolean isTradeType(){
		return this.type == Transaction.Type.Buy || this.type == Transaction.Type.Sell ;
	}

	public String getTypeShort(){
		return this.type.toString().substring(0,4);
	}

	public BigDecimal getTotalFrom(){

		return this.quantityFrom.add(this.feeFrom);
	}

	@Override
	public String toString() {
		return "[type=" + type + ", id=" + id + ", externalId=" + externalId
				+ ", timestamp=" + timestamp
				+ ", quantity=" + quantity

				+ ", exchange=" + exchange + ", assetFrom=" + assetFrom + ", assetTo=" + assetTo + "]";
	}

	public String toStringPreInit() {
		return "Transaction [type=" + type + ", id=" + id + ", externalId=" + externalId + ", timestamp=" + timestamp
				+ ", asset=" + asset + ", market=" + Arrays.toString(market) + "]";
	}


}
