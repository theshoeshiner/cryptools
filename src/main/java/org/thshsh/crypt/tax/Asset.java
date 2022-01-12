package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Asset {

	public static final Logger LOGGER = LoggerFactory.getLogger(Asset.class);

	public static String FIAT_ASSET = "USD";

	String name;
	List<BuyRecord> buyRecords;
	List<SellRecord> sellRecords;
	List<Record> records;
	BigDecimal balance;

	Asset(String name) {
		this.name = name;
		this.buyRecords = new ArrayList<>();
		this.sellRecords = new ArrayList<>();
		this.records = new ArrayList<>();
		this.balance = BigDecimal.ZERO;
		//this.accounts = new Map();
	}

	void deposit(BigDecimal quantity, Transaction transaction){

		this.balance = this.balance.add(quantity);
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).add(quantity));
		LOGGER.info("Deposit {}: {} Balance: {}",new Object[] {this.name,quantity,this.balance});

		if(transaction.external && this.name == FIAT_ASSET) {
			//LOGGER.info("Deposit {}: {} Balance: {}",new Object[] {this.name,quantity,this.balance});
			fiatExternal = fiatExternal.add(quantity);
			LOGGER.info("fiatExternal: {}",fiatExternal);
		}

	}

	void withdraw(BigDecimal quantity, Transaction transaction){

		this.balance = this.balance.subtract(quantity);
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).subtract(quantity));

		/*if(transaction.external) {
			LOGGER.debug("Withdraw External: {} Asset: {} Balance: {}",new Object[] {quantity,this.name,this.balance});
		}*/
	}

	IncomeRecord income(BigDecimal quantity, Transaction transaction){

		this.balance = this.balance.add(quantity);
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).add(quantity));

		RecordGains gains = new RecordGains();
		gains.addIncome(transaction);

		IncomeRecord record = new IncomeRecord(transaction);
		record.gains = gains;
		this.records.add(record);

		LOGGER.debug("Income: {} {} Balance: {}",new Object[] {quantity,this.name,this.balance});
		return record;

	}

	BuyRecord buy(BigDecimal quantity, BigDecimal price, Transaction transaction) {



		this.balance = this.balance.add(quantity);
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).add(quantity));

		LOGGER.debug("Bought: {} {} Balance: {}",new Object[] {quantity,this.name,this.balance});

		if(!this.name.equals(FIAT_ASSET)) {


			//var time = transaction.time;
			LOGGER.info("Buy: {} {} For: {} {} / {} USD",new Object[] {quantity,this.name,transaction.getTotalFrom(),transaction.assetFrom,price});

			BuyRecord r = new BuyRecord(quantity, price, transaction);
			r.asset = this;
			this.buyRecords.add(r);
			this.records.add(r);

			return r;
		}



		return null;
		//console.log(this.name + " bought: " + quantity + " balance: " + this.balance);
	}

	BigDecimal fiatExternal = new BigDecimal(0);

	SellRecord sell(BigDecimal quantity, BigDecimal price, Transaction transaction) {



		//if the transaction is external and a buy, then the thing we're selling is not part of our accounts
		/*if(transaction.external && transaction.type == Type.Buy) {
			if (this.name == FIAT_ASSET) {
				fiatExternal = fiatExternal.add(quantity);
				LOGGER.info("fiatExternal: {}",fiatExternal);
			}
		}
		else {*/
			this.balance = this.balance.subtract(quantity);
			transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).subtract(quantity));
			LOGGER.debug("Sell: {} Asset: {} New Balance: {}",new Object[] {quantity,this.name,this.balance});
		//}



		//selling fiat is not a taxable transaction, so we dont need to create tax records
		//TODO track FIAT
		if (this.name == FIAT_ASSET) {
			return null;
		}
		else {

			SellRecord sellBlock = new SellRecord(quantity, price,transaction);
			sellBlock.asset = this;

			//var gains = this.sellFifo(sell);
			RecordGains gains = this.sellMlmg(sellBlock);
			sellBlock.gains = gains;
			this.sellRecords.add(sellBlock);
			this.records.add(sellBlock);


			//this.addToTable(sellBlock);

			return sellBlock;
		}
	}

	RecordGains sellMlmg(SellRecord sell) {

		//need to see if this sell record is going to end up being a wash

		//Asset self = this;
		LOGGER.info("sellMlmg: {}",sell);
		LOGGER.debug("check records: {}",this.buyRecords.size());

		Map<BuyRecord,GainSort> sortMap = new HashMap<>();
		List<GainSort> sorted = new ArrayList<>();

		for(BuyRecord record : this.buyRecords){
			Boolean isShort = Accounts.isShortTerm(record.timestamp, sell.timestamp);
			//var isLoss = record.pricePer.gt(sell.pricePer);
			BigDecimal gainPer = sell.pricePer.subtract(record.pricePer);
			GainSort gs = new GainSort(sell,record,isShort,gainPer);
			sortMap.put(record,gs);
			sorted.add(gs);
		}

		//sorted.sort(this.taxCategorySort);
		//sorted.sort(this.taxAmountSort);
		Collections.sort(sorted,Asset::taxAmountSort);

		LOGGER.debug("Sorted buy records");

		if(sell.transaction.id == "1858647934") {
			LOGGER.info("Sorted records...");
			for(GainSort gs : sorted){
				//var s1 = sortMap.get(record);
				BuyRecord record = gs.record;
				if(gs.hasBalance) {
					LOGGER.info("Record: {}, {} = {}",new Object[] {record.id,gs,record});
				}
			}
		}


		RecordGains gains = new RecordGains();
		//Integer saleIndex = 1;

		List<Sale> shortTermSales = new ArrayList<>();
		List<Sale> longTermSales = new ArrayList<>();

		for (int i = 0; i < sorted.size() && sell.balance.compareTo(BigDecimal.ZERO) > 0; i++) {
			GainSort gainSort = sorted.get(i);
			BuyRecord buy = gainSort.record;
			LOGGER.debug("checking record: {}",buy);

			Sale sale = this.getSaleRecord(buy,sell);
			if(sale != null){
				sale.id = sell.id+"-"+buy.id;
				sell.saleRecords.add(sale);
				if (Accounts.isShortTerm(buy.timestamp, sell.timestamp)) {
					gains.addShortTerm(sale);
					shortTermSales.add(sale);
					sale.shortTerm = true;
				}
				else {
					gains.addLongTerm(sale);
					longTermSales.add(sale);
					sale.shortTerm = false;
				}
			}

		}

		//aggregate sale records
		if(shortTermSales.size() > 0){
			//return new Sale(basis,proceeds,take,buy,sell);
			Sale agg = new Sale(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,null,null);
			agg.id = sell.id+"-short";
			for(Sale sr : shortTermSales) {
				agg.aggregate(sr);
			}
			sell.shortTermAggregate = agg;
		}

		if(longTermSales.size() > 0){
			//return new Sale(basis,proceeds,take,buy,sell);
			Sale agg = new Sale(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,null,null);
			agg.id = sell.id+"-long";
			for(Sale sr : longTermSales) {
				agg.aggregate(sr);
			}
			sell.longTermAggregate = agg;
		}

		//its possible to have short term / gains losses in the sale Sell record because we might be selling different tax lots with different cost basises

		return gains;
		//return [gainsS, gainsL];

	}

	Sale getSaleRecord(BuyRecord buy,SellRecord sell) {

		if (buy.balance.compareTo(BigDecimal.ZERO) > 0) {
			//console.debug("buy has: " + buy.balance);
			LOGGER.debug("buy has: {}",buy.balance);
			BigDecimal left = sell.balance;
			BigDecimal take = left;
			//if the buy record doesn't have enough then take all of it
			if (buy.balance.compareTo(left) <= 0) take = buy.balance;
			//console.debug("taking: " + take);
			LOGGER.debug("taking: {}",take);
			BigDecimal basis = take.multiply(buy.pricePer);
			LOGGER.debug("basis at: {} = {}",new Object[] {buy.pricePer,basis});
			//console.debug("basis at: " + buy.pricePer + " = " + basis);
			sell.balance = left.subtract(take);
			buy.balance = buy.balance.subtract(take);
			//basis = basis.add(ba);
			BigDecimal proceeds = take.multiply(sell.pricePer);

			//console.debug("left: " + sell.balance);
			LOGGER.debug("left: {}",sell.balance);

			return new Sale(basis,proceeds,take,buy,sell);


		}
		return null;
	}

	static int taxAmountSort(GainSort s1,GainSort s2) {

		int comp = 0;
		//if one is empty, sort it to the end
		if(s1.hasBalance != s2.hasBalance){
			if(s1.hasBalance) comp = -1;
			else comp = 1;
		}
		else if(s1.isLoss == s2.isLoss){
			//both are the same (loss/gain)
			if(s1.isLoss) {

				//both are a loss, find the greatest loss
				int pc = s2.taxPer.compareTo(s1.taxPer);
				if(pc == 0) {
					//losses are the same, there is no great way to continue to sort, so nearest first
					comp = s2.timestamp.compareTo(s1.timestamp);
				}
				else comp = pc;

				/*
				if(s1.isShort == s2.isShort) {

					//both are short losses, so highest price to front (causes largest loss)
					var pc = s2.pricePer.compare(s1.pricePer);
					if(pc == 0) {
						//compare timestamps, for short losses return the oldest (smallest timestamp) first, this will leave more records in the short term range for longer time
						return s1.timestamp - s2.timestamp;
					}
					comp = pc;
				}
				else {
					//one is a short term loss, to sort it to the front
					if(s1.isShort) comp = -1;
					else comp = 1;
				}
				*/
			}
			else {
				//both are a gain

				//return the smallest taxes first
				int pc = s1.taxPer.compareTo(s2.taxPer);
				if(pc == 0) {
					//gains are the same, there is no great way to continue to sort, so return youngest first
					comp = s2.timestamp.compareTo(s1.timestamp);
				}
				else comp = pc;

				/*
				if(s1.isShort == s2.isShort) {
					//both are short gains, so push higher price to the front (causes less profit)
					var pc = s2.pricePer.compare(s1.pricePer);
					if(pc == 0) {
						//compare timestamps, for short gains return the youngest (largest timestamp) first
						return s2.timestamp - s1.timestamp;
					}
					comp = pc;
				}
				else {
					//one is short tern gain, so sort it to the end
					if(s1.isShort) comp = 1;
					else comp = -1;
				}
				*/
			}

		}
		else {
			//one is a loss, one is a gain, losses always go to the front
			if(s1.isLoss) comp = -1;
			else comp = 1;
		}

		//self.LOGGER.info("Compare {} vs {} = {}",[s1,s2,comp]);

		return comp;

	}

}
