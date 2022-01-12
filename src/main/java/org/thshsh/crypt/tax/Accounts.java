package org.thshsh.crypt.tax;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Accounts {

	public static final Logger LOGGER = LoggerFactory.getLogger(Accounts.class);

	Map<String,Asset> assetMap;
	Map<String,Account> accountMap;
	List<RecordGains> gainsList;
	List<Record> records;

	public Accounts() {
		this.assetMap = new HashMap<>();
		this.accountMap = new HashMap<>();
		//holds the list of short and long term gains from every transaction
		this.gainsList = new ArrayList<>();
		this.records = new ArrayList<>();
		//this.LOGGER = LoggerFactory.getLogger("Accounts");
	}

	public Account getAccount(String name){
		if (!this.accountMap.containsKey(name)) {
			Account a = new Account(name);
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
		//LOGGER.info("external: {}",t.external);
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
			SellRecord sellRecord = from.sell(t.quantityFrom.add(t.feeFrom), t.fiatTo, t);
			if(sellRecord!=null) this.records.add(sellRecord);


			if (sellRecord != null) {

				RecordGains gains = sellRecord.gains;
				gains.transaction = t;
				this.gainsList.add(gains);


			}

		}

	}

	static Boolean isShortTerm(LocalDateTime t0,LocalDateTime t1) {
		Long diff = Accounts.getDaysBetween(t0,t1);
		return diff <= 365;
	}

	static Long getDaysBetween(LocalDateTime t0,LocalDateTime t1){

		//console.log("getDaysBetween "+t0+" "+t1);

		return ChronoUnit.DAYS.between(t0, t1);

		/*Integer y0 = t0.getYear();
		Integer y1 = t1.getYear();

		if(y1 < y0) return false;

		var d0 = t0.getDOY();
		var d1 = t1.getDOY();

		//console.log("DOY = {} / {}",[d0,d1]);

		var add = 0;
		for(var i=y0;i<y1;i++){
			add+=Date.getDaysInYear(i);
		}

		//console.log("add: {}",[add]);

		var diff = d1-d0+add;

		//console.log("diff between "+t0+" and "+t1+" = "+diff);

		return diff;*/
	}

}
