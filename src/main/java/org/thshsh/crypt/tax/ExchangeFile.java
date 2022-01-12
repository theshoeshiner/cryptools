package org.thshsh.crypt.tax;

import java.util.HashMap;
import java.util.Map;

public class ExchangeFile {

	String url;
	String type;
	Map<Column,Integer> columns;
	Map<String,Object> force;
	Boolean negativePrice;
	Boolean negativeQuantity;
	Boolean allowDuplicates;
	Integer priority = 100;

	String exchange;

	public ExchangeFile(String url) {
		this(url,null);
	}

	public ExchangeFile(String url,String type) {
		this.url = url;
		this.type = type;
		this.columns = new HashMap<>();
		this.force = new HashMap<>();
		this.negativePrice = false;
		this.negativeQuantity = false;
		this.allowDuplicates = true;
	}

	public void mapColumn(Column type,Integer index){
		this.columns.put(type, index);

	}

	public void forceColumn(String key,String value){
		this.force.put(key,value);
	}



	/*
	 Column.Timestamp = "Timestamp";
Column.Id = "Id";
Column.Type = "Type"; //Transaction.Type
Column.Market = "Market"; //left is base, right is quote , BUY=(to-from) and SELL=(from-to)
Column.MarketInverse = "MarketInverse"; //when we need to flip the market,
Column.Asset = "Asset"; //when there's no market we assume the quote is USD
Column.Price = "Price"; //the price is always in quote asset (on the right) and represents the total of the order, fee not included, which means we need to subtract it?
Column.PriceWithFee = "PriceWithFee"; //same as above but includes fee (coinbase pro)
Column.Fee = "Fee"; //fee is also in the quote asset usually
Column.Quantity = "Quantity"; //quantity is always the base asset (on the left)
Column.Remaining = "Remaining"; //subtract from quantity
Column.Address = "Address";
Column.Description = "Description";
	 */


	@Override
	public String toString() {
		return "ExchangeFile [url=" + url + ", type=" + type + ", exchange=" + exchange + "]";
	}



	public static enum Column {
		Timestamp,Id,Type,Market,MarketInverse,Asset,Price,PriceWithFee,Fee,Quantity,Remaining,Address,Description;
	}
}
