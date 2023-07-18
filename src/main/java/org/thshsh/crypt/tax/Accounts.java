package org.thshsh.crypt.tax;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Accounts {

	public static final Logger LOGGER = LoggerFactory.getLogger(Accounts.class);

	Map<String,Asset> assetMap;
	Map<String,ExchangeAccount> accountMap;
	List<Gains> gainsList;
	List<Record> records;

	public Accounts() {
		this.assetMap = new HashMap<>();
		this.accountMap = new HashMap<>();
		//holds the list of short and long term gains from every transaction
		this.gainsList = new ArrayList<>();
		this.records = new ArrayList<>();
		//this.LOGGER = LoggerFactory.getLogger("Accounts");
	}

	public ExchangeAccount getAccount(String name){
		if (!this.accountMap.containsKey(name)) {
			ExchangeAccount a = new ExchangeAccount(name);
			this.accountMap.put(name, a);
		}
		return this.accountMap.get(name);
	}

	public Asset getAsset(String name) {
		if (!this.assetMap.containsKey(name)) {
			LOGGER.info("new asset: {}",name);
			if(name == null) throw new NullPointerException();
			Asset a = new Asset(name);
			this.assetMap.put(name, a);
		}
		return this.assetMap.get(name);
	}

	public void processTransaction(Transaction t) {

		LOGGER.info("processTransaction: {}",t);
		t.account = this.getAccount(t.exchange);

		if(t.type == Transaction.Type.Deposit) {
			Asset to = this.getAsset(t.assetTo);
			//TODO do we want to calculate fees here?
			//to.buy(t.quantityTo, new BigNum(0), t);
			to.deposit(t.quantityTo,t);
		}
		else if(t.type == Transaction.Type.Withdrawal) {
			Asset from = this.getAsset(t.assetFrom);
			from.withdraw(t.quantityFrom,t);
			//TODO specific method for charging fees
			if(t.feeFrom!=null)from.withdraw(t.feeFrom,t);
		}
		else if(t.type == Transaction.Type.Income) {
			Asset to = this.getAsset(t.assetTo);
			
			IncomeRecord record = to.income(t.quantityTo,t);
			this.records.add(record);
		}
		else if (t.type == Transaction.Type.Buy|| t.type == Transaction.Type.Sell) {


			Asset from = this.getAsset(t.assetFrom);
			Asset to = this.getAsset(t.assetTo);

			//apply fees here so that it shows what we actually got
			//create a buy record that adds the quantity to the "TO" asset
			//FIXME should send these fees separately so we can track them in the asset class
			BuyRecord buyRecord = to.buy(t.quantityTo.subtract(t.feeTo), t.fiatFrom.add(t.fiatFeeFrom), t);
			if(buyRecord!=null) this.records.add(buyRecord);

			//Create a sell record that removes the quantity from the "FROM" asset
			//We combine the total and the fee so that we remove the correct amount
			//We also subtract the fee from the fiatTo value, so we show correct proceeds
			SellRecord sellRecord = from.sell(t.quantityFrom.add(t.feeFrom), t.fiatTo.subtract(t.fiatFeeTo), t);
	
			if (sellRecord != null) {
				this.records.add(sellRecord);
				this.gainsList.add(sellRecord.gains);

			}

		}

	}
	
	

	public List<Record> getRecords() {
		return records;
	}

	public Stream<SaleAggregate> getTaxableRecords(){
		
		return getRecords().stream()
				.filter(r -> r instanceof SellRecord)
				.flatMap(r -> {
					SellRecord sr = (SellRecord) r;
					List<SaleAggregate> aggs = new ArrayList<>();
					if(sr.longTermAggregate != null) aggs.add(sr.longTermAggregate);
					if(sr.shortTermAggregate != null) aggs.add(sr.shortTermAggregate);
					return aggs.stream();
				});
		
	}
	
	static Boolean isShortTerm(ZonedDateTime t0,ZonedDateTime t1) {
		Long diff = Accounts.getDaysBetween(t0,t1);
		return diff <= 365;
	}

	static Long getDaysBetween(ZonedDateTime t0,ZonedDateTime t1){
		return ChronoUnit.DAYS.between(t0, t1);
	}

}
