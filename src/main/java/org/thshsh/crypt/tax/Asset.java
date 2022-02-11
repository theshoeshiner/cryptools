package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ComparatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Asset {

	public static final Logger LOGGER = LoggerFactory.getLogger(Asset.class);

	public static String FIAT_ASSET = "USD";

	String name;
	List<Record> buyRecords;
	List<SellRecord> sellRecords;
	List<Record> records;
	BigDecimal balance;

	BigDecimal fiatExternal = new BigDecimal(0);
	
	Asset(String name) {
		this.name = name;
		this.buyRecords = new ArrayList<>();
		this.sellRecords = new ArrayList<>();
		this.records = new ArrayList<>();
		this.balance = BigDecimal.ZERO;
		//this.accounts = new Map();
	}
	
	//Deposit and Withdraw do not affect buy/sell records, but they do affect the total asset balance

	void deposit(BigDecimal quantity, Transaction transaction){

		BigDecimal newBalance = this.balance.add(quantity);
		LOGGER.info("{} Deposit: {}  Current: {} New: {}",new Object[] {this.name,quantity,this.balance,newBalance});
		this.balance = newBalance;
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).add(quantity));
		//LOGGER.info("Deposit {}: {} Balance: {}",new Object[] {this.name,quantity,this.balance});

		//TODO better way to track external deposits
		if(transaction.external && this.name == FIAT_ASSET) {
			fiatExternal = fiatExternal.add(quantity);
			LOGGER.info("fiatExternal: {}",fiatExternal);
		}

	}

	void withdraw(BigDecimal quantity, Transaction transaction){

		BigDecimal newBalance = this.balance.subtract(quantity);
		LOGGER.info("{} Withdraw: {}  Current: {} New: {}",new Object[] {this.name,quantity,this.balance,newBalance});
		this.balance = newBalance;
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).subtract(quantity));

	}

	IncomeRecord income(BigDecimal quantity, Transaction transaction){

		this.balance = this.balance.add(quantity);
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).add(quantity));

		Gains gains = new Gains();
		gains.addIncome(transaction);

		IncomeRecord record = new IncomeRecord(this,transaction.quantityTo,transaction.fiatTo,transaction);
		record.gains = gains;
		this.records.add(record);
		this.buyRecords.add(record);

		LOGGER.debug("Income: {} {} Balance: {}",new Object[] {quantity,this.name,this.balance});
		return record;

	}

	BuyRecord buy(BigDecimal quantity, BigDecimal price, Transaction transaction) {


		this.balance = this.balance.add(quantity);
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).add(quantity));

		//LOGGER.debug("Bought: {} {} Balance: {}",new Object[] {quantity,this.name,this.balance});

		if(!this.name.equals(FIAT_ASSET)) {

			LOGGER.info("Buy: {} {} For: {} {} / {} USD",new Object[] {quantity,this.name,transaction.getTotalFrom(),transaction.assetFrom,price});

			BuyRecord r = new BuyRecord(quantity, price, transaction,this);
			this.buyRecords.add(r);
			this.records.add(r);

			LOGGER.debug("Bought {}: {} Balance: {}",new Object[] {this.name,quantity,this.balance});
			
			return r;
		}



		return null;
		//console.log(this.name + " bought: " + quantity + " balance: " + this.balance);
	}

	

	SellRecord sell(BigDecimal quantity, BigDecimal price, Transaction transaction) {

		this.balance = this.balance.subtract(quantity);
		if(this.balance.compareTo(BigDecimal.ZERO)<0) throw new IllegalStateException("Asset Balance is < 0");
		
		transaction.account.setBalance(this.name,transaction.account.getBalance(this.name).subtract(quantity));
		LOGGER.debug("Sell: {} Asset: {} New Balance: {}",new Object[] {quantity,this.name,this.balance});

		//selling fiat (ie buying a crypto with fiat) is not a taxable transaction, so we dont need to create tax records
		//TODO track FIAT
		if (this.name == FIAT_ASSET) {
			return null;
		}
		else {

			SellRecord sellBlock = new SellRecord(quantity, price,this,transaction);
			
			Gains gains = this.processSell(sellBlock);
			sellBlock.gains = gains;
			this.sellRecords.add(sellBlock);
			this.records.add(sellBlock);

			return sellBlock;
		}
	}

	Gains processSell(SellRecord sell) {

		//need to see if this sell record is going to end up being a wash

		//Asset self = this;
		LOGGER.info("sellMlmg: {}",sell);
		LOGGER.debug("check records: {}",this.buyRecords.size());

		Map<Record,GainSort> sortMap = new HashMap<>();
		List<GainSort> sortedBuyRecords = new ArrayList<>();

		for(Record record : this.buyRecords){
			//only add records that have a balance
			if(record.balance.compareTo(BigDecimal.ZERO) > 0) {
				Boolean isShort = Accounts.isShortTerm(record.timestamp, sell.timestamp);
				//gain per asset share
				BigDecimal gainPer = sell.pricePer.subtract(record.pricePer);
				GainSort gs = new GainSort(sell,record,isShort,gainPer);
				sortMap.put(record,gs);
				sortedBuyRecords.add(gs);
			}
		}

		//sorted.sort(this.taxCategorySort);
		//sorted.sort(this.taxAmountSort);
		//List<GainSort> copy = new ArrayList<GainSort>(sortedBuyRecords);
		
		
		//Collections.sort(sortedBuyRecords,Asset::totalTaxSort);
		Collections.sort(sortedBuyRecords,Asset::taxRateSort);
		
		/*Collections.sort(copy,Asset::taxRateSort);
		if(!sortedBuyRecords.equals(copy)) {
			LOGGER.info("Lists were not equal");
			
			LOGGER.info("taxAmountSort");
			for(int i=0;i<20 && i<sortedBuyRecords.size();i++) {
				LOGGER.info("Record: {}",sortedBuyRecords.get(i).record);
				LOGGER.info("Sort: {}",sortedBuyRecords.get(i));
			}
			
			LOGGER.info("taxRateSort");
			for(int i=0;i<20 && i<copy.size();i++) {
				LOGGER.info("Record: {}",copy.get(i).record);
				LOGGER.info("Sort: {}",copy.get(i));
			}
		}*/
		
	
		
		

		LOGGER.debug("Sorted buy records");
		
		

		Gains gains = new Gains();

		List<Sale> shortTermSales = new ArrayList<>();
		List<Sale> longTermSales = new ArrayList<>();

		
		//Go through sorted buy records and sell portions of each until we are done
		for (int i = 0; i < sortedBuyRecords.size() && sell.balance.compareTo(BigDecimal.ZERO) > 0; i++) {
			GainSort gainSort = sortedBuyRecords.get(i);
			Record buy = gainSort.record;
			LOGGER.debug("checking gainSort: {}",gainSort);
			LOGGER.debug("checking record: {}",buy);

			Sale sale = this.sellFromBuyRecord(buy,sell);
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
		
		if(sell.balance.compareTo(BigDecimal.ZERO) > 0) {
			throw new IllegalStateException("Could not find assets to sell");
		}

		//combine short and long term sales into single aggregate record
		if(shortTermSales.size() > 0){
			sell.shortTermAggregate =  new SaleAggregate(sell.id+"-short",sell, shortTermSales);
		}

		if(longTermSales.size() > 0){
			sell.longTermAggregate = new SaleAggregate(sell.id+"-long",sell,longTermSales);
		}

		//its possible to have short term / gains losses in the same Sell record because we might be selling different tax lots with different cost basises

		return gains;

	}

	Sale sellFromBuyRecord(Record buy,SellRecord sell) {

		if (buy.balance.compareTo(BigDecimal.ZERO) > 0) {
			//console.debug("buy has: " + buy.balance);
			LOGGER.debug("buy has: {}",buy.balance);
			LOGGER.debug("price per: {}",buy.pricePer);
			BigDecimal left = sell.balance;
			BigDecimal take = left;
			//if the buy record doesn't have enough then take all of it
			if (buy.balance.compareTo(left) <= 0) take = buy.balance;
			//console.debug("taking: " + take);
			LOGGER.debug("taking: {}",take);
			BigDecimal basis = take.multiply(buy.pricePer);
			LOGGER.debug("total basis: {}",basis);
			//console.debug("basis at: " + buy.pricePer + " = " + basis);
			sell.balance = left.subtract(take);
			buy.balance = buy.balance.subtract(take);
			//basis = basis.add(ba);
			BigDecimal proceeds = take.multiply(sell.pricePer);

			//console.debug("left: " + sell.balance);
			LOGGER.debug("left to sell: {}",sell.balance);

			return new Sale(basis,proceeds,take,buy,sell);


		}
		return null;
	}
	
	/** 
	 * This is a tax optimized sort, in that we optimize the rate that we are saving at, but not the actual savings
	 * This is not much different from MLMG in early years since there are fewer lots to choose from
	 * But in later years this will result in higher taxes because it will ALWAYS choose long term over short term, regardless of total gain/tax
	 * @param s1
	 * @param s2
	 * @return
	 */
	static int taxRateSort(GainSort s1,GainSort s2) {
		int comp = 0;
		
		//sort losses to front
		comp = s2.isLoss.compareTo(s1.isLoss);
		if(comp == 0) {
			//sort by tax rate
			comp = s1.taxRate.compareTo(s2.taxRate);
			//if its a loss then we want higher tax rates first (this should auto account for long/short term, right?)
			if(s1.isLoss) comp = comp * -1;
			if(comp == 0) {
				//sort by actual gain/loss
				comp = s1.gainPer.compareTo(s2.gainPer);
				//if it's a gain then we want greater losses at the top
				//if(s1.isLoss) comp = comp *-1;
			}
		}
		
		
		return comp;
	}
	
	/**
	 * This sorts based on the ACTUAL tax owed. Even if it results in a short term sell
	 * As long as the sell produced less overall taxes
	 * @param s1
	 * @param s2
	 * @return
	 */
	static int totalTaxSort(GainSort s1,GainSort s2) {
		int comp = 0;
		
		//sort losses to front
		//comp = s2.isLoss.compareTo(s1.isLoss);
		//if(comp == 0) {
			//sort by actual taxes
		comp = s1.taxPer.compareTo(s2.taxPer);
		//if its a loss then we want higher taxes first
		//if(s1.isLoss) comp = comp * -1;
		if(comp == 0) {
			LOGGER.info("TaxPer was same for {} and {}",s1,s2);
			//sort by oldest timestamps
			comp = s1.record.timestamp.compareTo(s2.record.timestamp);
			if(comp == 0) {
				LOGGER.info("timestamp was same for {} and {}",s1,s2);
			}
			//if it's a gain then we want greater losses at the top
			//if(s1.isLoss) comp = comp *-1;
		}
		//}
		
		
		return comp;
	}

	
	

	public String getName() {
		return name;
	}
	
	

	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[name=");
		builder.append(name);
		builder.append(", balance=");
		builder.append(balance);
		builder.append("]");
		return builder.toString();
	}

	
	
}
