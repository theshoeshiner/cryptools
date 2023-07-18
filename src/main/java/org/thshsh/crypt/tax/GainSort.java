package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
		//FIXME These are individual tax brackets, not married - might have changed previous years?
		
		/*	TAX_BRACKETS.put(2017,new BigDecimal(".28"));
			TAX_BRACKETS.put(2018,new BigDecimal(".32"));
			TAX_BRACKETS.put(2019,new BigDecimal(".24"));
			TAX_BRACKETS.put(2020,new BigDecimal(".32"));
			TAX_BRACKETS.put(2021,new BigDecimal(".32"));*/
		
		TAX_BRACKETS.put(2017,new BigDecimal(".28"));
		TAX_BRACKETS.put(2018,new BigDecimal(".24"));
		TAX_BRACKETS.put(2019,new BigDecimal(".24"));
		TAX_BRACKETS.put(2020,new BigDecimal(".24"));
		TAX_BRACKETS.put(2021,new BigDecimal(".22"));
		//this is correct
		TAX_BRACKETS.put(2022,new BigDecimal(".22"));
	}

	static BigDecimal LONG_TERM_TAX = new BigDecimal(".15");

	SellRecord sellRecord;
	Record record;
	String id;
	Boolean isShort;
	BigDecimal gainPer;
	BigDecimal gainPerAbs;
	Boolean isLoss;
	BigDecimal pricePer;
	//Boolean hasBalance;
	BigDecimal taxPerAbs;
	BigDecimal taxPer;
	ZonedDateTime timestamp;
	BigDecimal taxRate;
	
	//BigDecimal taxGainRatio;

	GainSort(SellRecord sell,Record record,Boolean isShort) {
		this.sellRecord = sell;
		this.record = record;
		this.id = record.id;
		this.isShort = isShort;
		this.gainPer = sell.pricePer.subtract(record.pricePer);
		this.gainPerAbs = gainPer.abs();
		this.isLoss = gainPer.compareTo(BigDecimal.ZERO) < 0;
		this.pricePer = record.pricePer;
		//this.hasBalance = record.balance.compareTo(BigDecimal.ZERO) > 0;
		//this.LOG4JSSTRINGABLE=true;
		this.timestamp = record.timestamp;
		
		this.taxRate = (isShort)?TAX_BRACKETS.get(this.sellRecord.timestamp.getYear()):LONG_TERM_TAX;

		this.taxPer = this.gainPer.multiply(taxRate);
		
		this.taxPerAbs = taxPer.abs();
		
		//this.taxGainRatio = this.taxPer.divide(this.gainPer);
	}

	public String toString(){
		return 
				"Record: "+record.id
				+", Balance: "+this.record.balance+" "+(this.isShort?"Short":"Long")+"-Term "+(this.isLoss?"Loss":"Gain")
				+", Price: "+this.pricePer
				+", GainPer: "+this.gainPer
				+", TaxRate: "+this.taxRate
				+", TaxPer: "+this.taxPerAbs
				//+", TaxGainRatio: "+this.taxGainRatio
				;
	}

}
