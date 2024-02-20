package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.thshsh.crypt.MarketRate;

public class Transaction {

	//public static final String FIELD_TYPE = "type";
	
	public enum Field {
		Type;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
		
	}
	
	public enum Type {
		Buy,Sell,Deposit,Withdrawal,Income;
		public Type opposite() {
			switch(this) {
			case Buy:
				return Sell;
			case Deposit:
				return Withdrawal;
			case Sell:
				return Buy;
			case Withdrawal:
				return Deposit;
			case Income:
				throw new IllegalArgumentException("Income has no opposite");
			default:
				throw new IllegalArgumentException();
			
			}
		}
		//,Delist;
	}

	//raw fields from load
	public Type type;
	String id;
	String externalId;
	ZonedDateTime timestamp;

	//raw quantity regardless of buy/sell
	BigDecimal quantity;
	//fee from the QUOTE asset (for a BUY this is the FROM asset)
	BigDecimal fee;
	//fee from the BASE asset, only used for coin metro
	BigDecimal feeInverse;
	//amount of order that was NOT exchanged
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
	//Boolean internal = false;
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
	ExchangeAccount account;
	
	MarketRate fromRate;
	MarketRate toRate;

	public Transaction(String id) {
		this.externalId = id;
		this.id = Integer.toUnsignedString(this.externalId.hashCode());
	}


	public void setTimestamp(ZonedDateTime ts) {
		this.timestamp = ts;
		this.time = ts.toEpochSecond();
	}


	public Transaction(String id,ZonedDateTime timestamp,Type type,BigDecimal quantity,String asset,String exchange,BigDecimal price,String[] market){

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

	public Transaction(String string, ZonedDateTime ofEpochSecond, Type deposit, BigDecimal total, String asset2,String exchange2) {
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
				+ ", market=" +  Arrays.toString(market)
				+ ", quantity=" + quantity
				+ ", price=" + price
				
				+ ", assetFrom=" + assetFrom
				+  ", assetTo=" + assetTo
				
				+ ", quantityFrom=" + quantityFrom
				+ ", quantityTo=" + quantityTo
				
				
				+ ", feeFrom=" + feeFrom
				+ ", feeTo=" + feeTo
				
				+ ", fiatFrom=" + fiatFrom
				+ ", fiatTo=" + fiatTo
				
				+ ", fiatFeeTo=" + fiatFeeTo
				+ ", exchange=" + exchange
				+ "]";
	}

	public String toStringPreInit() {
		return "Transaction [type=" + type + ", id=" + id + ", externalId=" + externalId + ", timestamp=" + timestamp
				+ ", asset=" + asset + ", market=" + Arrays.toString(market) + "]";
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public String getExchange() {
		return exchange;
	}

	public String getAssetFrom() {
		return assetFrom;
	}

	public String getAssetTo() {
		return assetTo;
	}

	public String getExternalId() {
		return externalId;
	}

	public MarketRate getFromRate() {
		return fromRate;
	}

	public MarketRate getToRate() {
		return toRate;
	}


	
	
	
}
