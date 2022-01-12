package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the necessary information to pick which BuyRecord needs to be sold.
 * @author TheShoeShiner
 *
 */
public class GainSort {

	static Map<Integer,BigDecimal> TAX_BRACKETS = new HashMap<>();
	static {
		TAX_BRACKETS.put(2017,new BigDecimal(.28));
		TAX_BRACKETS.put(2018,new BigDecimal(.32));
		TAX_BRACKETS.put(2019,new BigDecimal(.24));
		TAX_BRACKETS.put(2020,new BigDecimal(.32));
		TAX_BRACKETS.put(2021,new BigDecimal(.32));
	}

	static BigDecimal LONG_TERM_TAX = new BigDecimal(.15);

	SellRecord sellRecord;
	BuyRecord record;
	String id;
	Boolean isShort;
	BigDecimal gainPer;
	Boolean isLoss;
	BigDecimal pricePer;
	Boolean hasBalance;
	BigDecimal taxPer;
	LocalDateTime timestamp;



	GainSort(SellRecord sell,BuyRecord record,Boolean isShort,BigDecimal gainPer) {
		this.sellRecord = sell;
		this.record = record;
		this.id = record.id;
		this.isShort = isShort;
		this.gainPer = gainPer.abs();
		this.isLoss = gainPer.compareTo(BigDecimal.ZERO) < 0;
		this.pricePer = record.pricePer;
		this.hasBalance = record.balance.compareTo(BigDecimal.ZERO) > 0;
		//this.LOG4JSSTRINGABLE=true;
		this.timestamp = record.timestamp;


		this.taxPer = (isShort)?this.gainPer.multiply(TAX_BRACKETS.get(this.sellRecord.timestamp.getYear())):this.gainPer.multiply(LONG_TERM_TAX);
	}

	public String toString(){
		return "Balance: "+this.record.balance+" "+(this.isShort?"Short":"Long")+"-Term "+(this.isLoss?"Loss":"Gain")+" at Price: "+this.pricePer+" with Tax Per: "+this.taxPer;
	}

}
