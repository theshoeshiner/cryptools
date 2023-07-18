package org.thshsh.crypt.tax;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.crypt.tax.ExchangeFile.Column;
import org.thshsh.crypt.tax.Transaction.Type;

public class TransactionManager {

	public static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);

	//public static enum Column {
		//Timestamp,Id,Type,Market,MarketInverse,Asset,Price,PriceWithFee,Fee,Quantity,Remaining,Address,Description;
		/*Column.Id = "Id";
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
	//}
	
	public static final String FIAT_ASSET = "USD";

	List<ExchangeFile> files;
	Map<String,Transaction> externalIdTransactionMap;
	List<Transaction> transactionsList;
	
	/**
	 * Missing transfers are transactions where only one side of the transfer is present 
	 * and we need to create a phantom transaction for the missing side
	 * So far these are just the migrations from coinbase to coinbase pro, where coinbase does not list them as withdrawals
	 */
	Map<String,String> missingTransfers;
	/**
	 * List of ids for "external" transactions, which are untracked deposit/withdrawals to outside accounts
	 */
	List<String> externalIds;

	/**
	 * Force match Ids are transfers that we are unable to auto match but we want to force a match
	 * This is helpful when a transfer is broken into multiple pieces by an exchange or when
	 * for some reason the transfer amounts do not match because fees were not reported on the send side
	 */
	List<String> forceMatchIds;
	List<List<String>> forceMatchArrays;

	/**
	 * Orphaned transfers are transfers which, after processing, still dont have a match.
	 * For deposits we must consider them income.
	 */
	List<Transaction> orphanTransactions;

	List<DateTimeFormatter> formats = new ArrayList<>();

	Map<String,String> currencyNameMap = new HashMap<>();

	public TransactionManager(){
		//LOGGER = LoggerFactory.getLogger("TransactionManager");
		this.files = new ArrayList<>();
		this.externalIdTransactionMap = new HashMap<>();
		this.transactionsList = new ArrayList<>();
		this.externalIds = new ArrayList<>();
		//this.forceIncomeType = new Array();
		//this.internalIds = new ArrayList<>();
		this.missingTransfers = new HashMap<>();
		this.forceMatchIds = new ArrayList<>();
		this.forceMatchArrays = new ArrayList<>();
		this.orphanTransactions = new ArrayList<>();
		this.currencyNameMap.put("STRAT","STRAX");
		this.currencyNameMap.put("ZUSD","USD");
		this.currencyNameMap.put("XBT","BTC");

		formats.add(DateTimeFormatter.ISO_DATE_TIME);

		formats.add(new DateTimeFormatterBuilder()
	            .parseCaseInsensitive()
	            .append(DateTimeFormatter.ISO_LOCAL_DATE)
	            .optionalStart()
	            .appendLiteral('T')
	            .optionalEnd()
	            .optionalStart()
	            .appendLiteral(' ')
	            .optionalEnd()
	            .append(DateTimeFormatter.ISO_LOCAL_TIME)
	            .optionalStart()
	            .appendOffsetId()
	            .optionalStart()
	            .appendLiteral('[')
	            .parseCaseSensitive()
	            .appendZoneRegionId()
	            .appendLiteral(']')
	            .toFormatter());

		//java.lang.IllegalStateException: '12/2/2017 1:41:39 AM' Could not be parsed
		//formats.add(DateTimeFormatter.ofPattern("M/dd/yyyy [h]h:mm:ss a"));

		formats.add(DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"));
	}


	public void addExternalIds(List<String> ts){
		this.externalIds.addAll(ts);
	}

	public void forceMatch(List<String> ids) {
		//add all ids to the force match list
		this.forceMatchIds.addAll(ids);
		this.forceMatchArrays.add(ids);
	}

	public Transaction getTransactionByExternalId(String id){
		if(this.externalIdTransactionMap.get(id) == null){
			Transaction r = new Transaction(id);
			this.externalIdTransactionMap.put(id,r);
		}
		return this.externalIdTransactionMap.get(id);
	}



	public void mapRow(CSVRecord row,ExchangeFile file) {


		Map<Column,Integer> mapping = file.columns;
		String id = row.get(mapping.get(Column.Id));
		//var id = row[mapping[Column.Id]];
		if(id == null || id.length() == 0) {
			LOGGER.debug("Skipping row with no id: {}",row);
			return;
		}

		Transaction transaction = this.getTransactionByExternalId(id);

		try {

			if(transaction.loaded) {
				if(!file.allowDuplicates) {
					LOGGER.error("Ignoring Duplicate transaction by Id {} - {}",id,transaction);
					//throw "Duplicate transaction not allowed";
					return;
				}
			}
			transaction.external = this.externalIds.indexOf(transaction.externalId) > -1;
			//transaction.internal = this.internalIds.indexOf(transaction.externalId) > -1;
			transaction.exchange = file.exchange;
			transaction.file = file;

			if(file.force.size() > 0){
				file.force.forEach((key,val)->{
					try {

						Field f = Transaction.class.getField(key);
						f.set(transaction, val);
					}
					catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						throw new IllegalStateException(e);
					}

					//TODO
				/*file.force.forEach(function(val,key) {
					transaction[key]=val; */
				});

			}

			if(mapping.containsKey(Column.Timestamp)){

				String ts = row.get(mapping.get(Column.Timestamp));

				for(DateTimeFormatter dtf : formats) {
					try {
						LocalDateTime ldt = LocalDateTime.from(dtf.parse(ts));
						//convert to UTC and assume all local dates are already UTC
						ZonedDateTime zdt = ldt.atZone(ZoneId.of("Z"));
						//ldt = ldt.withZoneSameLocal(ZoneId.of("Z"));
						//LocalDateTime ldt = LocalDateTime.from(dtf.parse(ts));
						transaction.setTimestamp(zdt);
						break;
					}
					catch (DateTimeParseException e) {}
				}

				if(transaction.timestamp == null) throw new IllegalStateException("'"+ts+"' Could not be parsed");

			}

			if(mapping.containsKey(Column.Description)){
				transaction.description = row.get(mapping.get(Column.Description));
			}

			//see if this is a direct buy of crypto via USD
			//Bought 0.0500 BTC for $224.68 USD
			/*if(transaction.description.match(/Bought \d+ \w+ .* \d+ .* USD/) != null){
				LOGGER.warn("Found external description: {}",transaction);
			}*/
			//TODO this doesnt work because coinbase always uses this text even if we already had a USD balance

			//if(transaction.description != null && transaction.description.match(/Bought .* for .* USD/) != null){
				//LOGGER.warn("Found external description: {}",transaction);
			//}


			this.mapType(row,mapping,transaction);

			if(transaction.isTradeType()) {
				this.mapTrade(file,row,mapping,transaction);
			}
			else if(transaction.type == Transaction.Type.Deposit || transaction.type == Transaction.Type.Withdrawal || transaction.type == Transaction.Type.Income ){
				this.mapTransaction(row,mapping,transaction);
			}

			transaction.loaded = true;
			LOGGER.debug("transaction: {}",transaction);

		}
		catch(MappingException e){
			LOGGER.warn("mapping exception on transaction: {} - {}",transaction.toStringPreInit(),e);
			this.removeTransaction(id);
		}


	}

	public void removeTransaction(String id){
		this.externalIdTransactionMap.remove(id);
	}

	public void mapType(CSVRecord row, Map<Column,Integer> mapping, Transaction transaction) throws MappingException{
		if(mapping.containsKey(Column.Type)) {
			String typeString = row.get(mapping.get(Column.Type)).toLowerCase();
			Transaction.Type type;
			if(typeString.indexOf(Transaction.Type.Buy.toString().toLowerCase()) >-1) type = Transaction.Type.Buy;
			else if(typeString.indexOf(Transaction.Type.Sell.toString().toLowerCase()) >-1) type = Transaction.Type.Sell;
			else if(typeString.indexOf(Transaction.Type.Withdrawal.toString().toLowerCase()) >-1) type = Transaction.Type.Withdrawal;
			else if(typeString.indexOf("send") >-1) type = Transaction.Type.Withdrawal;
			else if(typeString.indexOf(Transaction.Type.Deposit.toString().toLowerCase()) >-1) type = Transaction.Type.Deposit;
			else if(typeString.indexOf("receive") >-1) type = Transaction.Type.Deposit;
			else if(typeString.indexOf("earn") >-1) type = Transaction.Type.Income;
			else if(typeString.indexOf("income") >-1) type = Transaction.Type.Income;
			else if(typeString.indexOf("order") >-1) type = null; //we dont know the type, and it must come from some other method
			else {
				//we dont know transaction type
				throw new MappingException( "Did not understand transaction type '"+typeString+"'");
			}
			if(type!=null) transaction.type = type;
		}
	}

	public void mapTransaction(CSVRecord row,Map<Column,Integer> mapping,Transaction transaction){

		if(mapping.containsKey(Column.Asset)) {
			//var asset = row[mapping[Column.Asset]];
			String asset = row.get(mapping.get(Column.Asset));
			transaction.asset = asset;
		}

		if(mapping.containsKey(Column.Quantity)) {
			//transaction.quantity = new BigNum(row[mapping[Column.Quantity]]).abs();
			transaction.quantity = toBigDecimal(row.get(mapping.get(Column.Quantity))).abs();
		}

		if(mapping.containsKey(Column.Fee)) {
			//transaction.fee = new BigNum(row[mapping[Column.Fee]]).abs();
			transaction.fee = toBigDecimal(row.get(mapping.get(Column.Fee))).abs();
		}

	}

	

	//Map<String,Asset>

	public String mapAssetName(String value) {

		if(value == null) return value;
		if(value.startsWith("XX")) value = value.substring(1);
		//if(value.equals("XBT"))value = "BTC";
		if(currencyNameMap.containsKey(value)) value = currencyNameMap.get(value);

		return value;
	}

	

	public void mapTrade(ExchangeFile file,CSVRecord row,Map<Column,Integer> mapping,Transaction transaction) throws MappingException{


		if(mapping.containsKey(Column.Market) || mapping.containsKey(Column.MarketInverse)) {
			String markets;
			if(mapping.containsKey(Column.Market)) {
				markets = row.get(mapping.get(Column.Market));
			}
			else {
				markets = row.get(mapping.get(Column.MarketInverse));
			}
			transaction.market = this.splitMarket(markets,mapping.containsKey(Column.MarketInverse));
			
			if(mapping.containsKey(Column.Asset)) {
				//if it contains an asset column as well then we need to use the asset column to swap the price and quantity
				//This is only setup to use for BUY transactions
				String asset = row.get(mapping.get(Column.Asset));
				LOGGER.info("Trade has asset: {}",asset);
				if(asset.equalsIgnoreCase(transaction.market[1])) {
					//swap markets
					//transaction.market = new String[] {transaction.market[1],transaction.market[0]};
					transaction.type = Type.Sell;
				}
				
			}
		}
		else if(mapping.containsKey(Column.Asset)) {
			//if we have an asset column then the other side is USD so asset is base and quote is USD
			String asset = row.get(mapping.get(Column.Asset));
			transaction.market = new String[] {asset,FIAT_ASSET};
		}
		else {
			//LOGGER.error("Transaction did not have proper markets: {}",row);
			throw new RuntimeException("Transaction did not have proper markets: "+row);
		}

		if(mapping.containsKey(Column.Quantity)) {
			BigDecimal quantity = toBigDecimal(row.get(mapping.get(Column.Quantity)));
			if(quantity.compareTo(BigDecimal.ZERO) < 0) {
				if(file.negativeQuantity) {
					//use quantity to swap type
					transaction.type = transaction.type.opposite();
				}
				else {
					//some exports report both sides of a trade, one with a negative quantity, but we can ignore that side
					throw new MappingException("Transaction had negative quantity: "+quantity);
				}
			}
			quantity = quantity.abs();
			transaction.quantity = quantity;
		}



		if(mapping.containsKey(Column.Fee)) {
			transaction.fee = toBigDecimal(row.get(mapping.get(Column.Fee))).abs();
		}

		//price not including fee
		if(mapping.containsKey(Column.Price)) {
			String price = row.get(mapping.get(Column.Price));
			transaction.price = toBigDecimal(price).abs();
		}
		else if(mapping.containsKey(Column.PriceWithFee)) {
			//for a buy we need to subtract it, for a sell we need to add it  (we will eventually subtract the fee independently)
			String price = row.get(mapping.get(Column.PriceWithFee));
			BigDecimal bn = toBigDecimal(price).abs();
			if(transaction.type == Transaction.Type.Buy) bn = bn.subtract(transaction.fee);
			else bn = bn.add(transaction.fee);
			transaction.price = bn;
		}

		if(mapping.containsKey(Column.Remaining)) {
			transaction.remaining = toBigDecimal( row.get(mapping.get(Column.Remaining)));
		}
		
		if(file.swapPriceForSell && transaction.type == Type.Sell) {
			BigDecimal q = transaction.price;
			transaction.price = transaction.quantity;
			transaction.quantity = q;
		}
		
		if(file.swapFeeForBuy && transaction.type == Type.Buy) {
			BigDecimal fee = transaction.fee;
			transaction.fee = transaction.feeInverse;
			transaction.feeInverse = fee;
		}

	}

	public String[] splitMarket(String market, Boolean inverse) {

		String splitRegex = "[\\\\\\-/]";
		String marketRegex = ".*?"+splitRegex+".*?";
		String[] pair;


		if(market.matches(marketRegex)) {
			pair = market.split(splitRegex);
		}
		else if(market.length()%2 == 0){
			//split evenly
			//pair = [market.substring(0,market.length/2),market.substring(market.length/2,market.length)];
			pair = new String[] {market.substring(0,market.length()/2),market.substring(market.length()/2,market.length())};
		}
		else {
			throw new RuntimeException( "Could not parse market: "+market);
		}

		//pair = [pair[0].toUpperCase(),pair[1].toUpperCase()];
		pair[0] = pair[0].toUpperCase();
		pair[1] = pair[1].toUpperCase();

		if(inverse) return new String[] {pair[1],pair[0]};
		else return pair;
	}

	public void initTransaction(Transaction transaction) {


		if(transaction.isTradeType()){

			if(transaction.remaining!=null) transaction.quantity = transaction.quantity.subtract(transaction.remaining);

			if(transaction.type == Transaction.Type.Buy) {
				transaction.assetFrom = transaction.market[1];
				transaction.quantityFrom = transaction.price;
				transaction.feeFrom = transaction.fee;
				transaction.feeTo = transaction.feeInverse;
				transaction.assetTo = transaction.market[0];
				transaction.quantityTo = transaction.quantity;
			}
			else if(transaction.type == Transaction.Type.Sell) {
				transaction.assetTo = transaction.market[1];
				transaction.quantityTo = transaction.price;
				transaction.feeTo = transaction.fee;
				transaction.feeFrom = transaction.feeInverse;
				transaction.assetFrom = transaction.market[0];
				transaction.quantityFrom = transaction.quantity;
			}

		}
		else if(transaction.type == Transaction.Type.Deposit || transaction.type == Transaction.Type.Withdrawal || transaction.type == Transaction.Type.Income ) {
			//if(transaction.address != null) {

			//}
			if(transaction.type == Transaction.Type.Deposit || transaction.type == Transaction.Type.Income) {
				transaction.quantityTo = transaction.quantity;
				transaction.assetTo = transaction.asset;
			}
			else if(transaction.type == Transaction.Type.Withdrawal) {
				transaction.quantityFrom = transaction.quantity;
				transaction.assetFrom = transaction.asset;
				transaction.feeFrom = transaction.fee;
			}
			
			

		}
		else {
			LOGGER.warn("Transaction not understood: {}",transaction);
			throw new RuntimeException( "Transaction not understood: "+transaction.type);
		}


		transaction.asset = this.mapAssetName(transaction.asset);
		transaction.assetFrom = this.mapAssetName(transaction.assetFrom);
		transaction.assetTo = this.mapAssetName(transaction.assetTo);

	}

	public void addFile(ExchangeFile file){
		this.files.add(file);
	}

	public void addTransaction(Transaction transaction){
		this.externalIdTransactionMap.put(transaction.externalId,transaction);
	}

	public void addMissingTransfer(String externalId,String exchange){
		//this.missingTransfers = [externalId,exchange];
		this.missingTransfers.put(externalId,exchange);
	}

	public void loadAndInit() throws IOException{
		//await
		this.loadFiles();

		LOGGER.info("loaded transactions: {}",this.externalIdTransactionMap.size());

		//await
		this.initTransactions();

		LOGGER.info("inited transactions: {}",this.transactionsList.size());
	}

	public void loadFiles() throws IOException{
		for(int i=0;i<this.files.size();i++){
			ExchangeFile f = this.files.get(i);
			//await
			//try {
				this.loadFile(f);
			//}
			//catch (IOException e) {
				//throw new RuntimeException(e);
			//}
		}
	}


	public void loadFile(ExchangeFile file) throws IOException{


		LOGGER.info("loading file: {}",file);

		Reader in = new InputStreamReader(TransactionManager.class.getResourceAsStream(file.url));
		Iterable<CSVRecord> records = CSVFormat
				.DEFAULT
				.withFirstRecordAsHeader()
				.withAllowMissingColumnNames()
				//.withSkipHeaderRecord()
				.withCommentMarker('#')
				.withIgnoreSurroundingSpaces()
				//.withHeader((String[])null)
				.parse(in);


		LOGGER.info("load file: {}",file);

		records.forEach(record -> {
			this.mapRow(record, file);

		});

	}

	protected void processTransactions() {

		this.externalIdTransactionMap.forEach( (key,transaction) -> {
			initAndAddTransaction(transaction);
		});

		LOGGER.info("Transactions: {}",this.transactionsList.size());
	}

	public void initAndAddTransaction(Transaction transaction) {
		this.initTransaction(transaction);
		//if(transaction.timestamp.toInstant().toEpochMilli()  < this.maxTimestamp) {
		this.transactionsList.add(transaction);
			/*}
			else {
				LOGGER.debug("Transaction past time: {}",transaction);
			}*/
	}

	protected void initTransactions(){

		LOGGER.info("initTransactions");

		this.processTransactions();

		this.removeDuplicates();

		this.createPhantomTransactions();

		this.matchTransfers();

		this.sortTransactions();

	}

	public void sortTransactions() {

		LOGGER.info("=======================SORTING=========================");

		Collections.sort(this.transactionsList, (o1,o2) -> {
			return o1.timestamp.compareTo(o2.timestamp);
		});

		Collections.sort(this.orphanTransactions, (o1,o2) -> {
			return o1.timestamp.compareTo(o2.timestamp);
		});

		/*this.transactionsList.sort(function (o1, o2) {
			return o1.timestamp - o2.timestamp;
		});

		this.orphanTransactions.sort(function (o1, o2) {
			return o1.timestamp - o2.timestamp;
		});*/

		LOGGER.info("Sorted {}",this.transactionsList.size());

		for(Transaction t : this.transactionsList) {
			LOGGER.info("Transaction: {}",t);
		}


	}

	public void matchTransfers() {

		/* Match transfers */


		LOGGER.info("=======================MATCHING TRANSFERS=========================");
		//var matched = 0;
		List<Transaction> matched = new ArrayList<>();
		this.orphanTransactions = new ArrayList<>();

		for(int i=0;i<this.transactionsList.size();i++){
			Transaction t = this.transactionsList.get(i);
			if(t.transfer == null && t.isTransferType()) {

				//check for force match
				if(this.forceMatchIds.indexOf(t.externalId) > -1) {
					//find which set it's in
					for(List<String> ar : this.forceMatchArrays) {
						if(ar.indexOf(t.externalId) > -1) {
							//collect all the real transactions from ids
							List<Transaction> transactions = new ArrayList<>();
							for(String trid : ar){
								transactions.add(this.getTransactionByExternalId(trid));
							}
							this.setMatch(transactions);
							matched.addAll(transactions);
							break;
						}
					}
				}
				else {
					Transaction other = this.findMatch(t);
					if(other != null) {
						this.setMatch(Arrays.asList(t,other));
						matched.add(t);
						matched.add(other);
					}
					else {
						LOGGER.debug("Orphan transfer: {}",t);
						this.orphanTransactions.add(t);
					}
				}

			}
		}

		LOGGER.info("Matched: {} Orphaned: {}",matched.size(),this.orphanTransactions.size());

		//any orphaned deposits need to marked as income
		for(Transaction orphan : this.orphanTransactions) {
			if(!orphan.external && orphan.type == Transaction.Type.Deposit) {
				LOGGER.info("Flagging orphan as income: {}",orphan);
				orphan.type = Transaction.Type.Income;
			}
		}

	}

	public void setMatch(List<Transaction> ts){
		Transfer tr = new Transfer(ts);
		LOGGER.debug("Created transfer: {}",tr);
		for(Transaction t : ts){
			t.transfer=tr;
		}
	}

	public Transaction findMatch(Transaction t0){
		Transaction.Type findType = t0.type == Transaction.Type.Deposit?Transaction.Type.Withdrawal:Transaction.Type.Deposit;
		Long rangeMs = 8*3600000l;
		BigDecimal quantityRange = new BigDecimal(.01f); //% fee
		Long time0 = t0.timestamp.toInstant().toEpochMilli();
		BigDecimal q0 = t0.quantity;
		for(Transaction t1 : this.transactionsList){
			//var t1 = this.transactionsList[i];
			Long time1 = t1.timestamp.toInstant().toEpochMilli();
			BigDecimal q1 = t1.quantity;
			if(t1.type == findType) {
				if( time0 <= (time1+rangeMs) && time0 >= (time1-rangeMs)){
					BigDecimal diff = q0.subtract(q1).abs();
					//if(diff.lte(q0.multiply(quantityRange)) && diff.lte(q1.multiply(quantityRange))){
					if(diff.compareTo(q0.multiply(quantityRange)) <= 0 && diff.compareTo(q1.multiply(quantityRange)) <= 0 ) {
						return t1;
					}
				}
			}
		}
		return null;
	}

	public void removeDuplicates(){

		/* Find and Remove Duplicates */

		List<Transaction> dups = new ArrayList<>();
		for(int i=0;i<this.transactionsList.size();i++){
			Transaction t = this.transactionsList.get(i);
			Transaction dup = this.findDuplicate(t);
			if(dup != null && dups.indexOf(dup) == -1){
				Transaction remove = t;
				Transaction keep = dup;
				if(t.file.priority < dup.file.priority) {
					remove = dup;
					keep = t;
				}
				keep.duplicate = remove;
				dups.add(remove);

			}
		}
		LOGGER.info("Found {} dups",dups.size());
		for(int i=0;i<dups.size();i++){
			Transaction t = dups.get(i);
			this.transactionsList.remove(t);
		}

	}

	/**
	Look for duplicates across exchanges. This is necessary because coinbase / CBP tend to duplicate transactions
	**/
	public Transaction findDuplicate(Transaction find){

		//LOGGER.info("find duplicate: {}",find.toStringPreInit());

		Integer rangeMs = 3600 * 2; //2 hours
		long time = find.time;
		Transaction.Type type = find.type;
		//String asset = find.asset;

		for(int i=0;i<this.transactionsList.size();i++){
			Transaction t = this.transactionsList.get(i);
			long time1 = t.time;
			//LOGGER.info("transaction: {}, {}",[t.type,t]);
			if(!t.exchange.equals(find.exchange)
				&& !t.id.equals(find.id)
				&& t.type == type
				&& (time1 >= time-rangeMs && time1 <= time+rangeMs)
				//&& t.asset == asset

				&& Objects.equals(t.assetFrom, find.assetFrom)
				&&  Objects.equals(t.assetTo, find.assetTo)
				){
				LOGGER.warn("Found duplicate transaction: {} vs {}",new Object[] {find.exchange,t.exchange});
				LOGGER.warn("Found duplicate transaction: {} vs {}",new Object[] {find,t});
				return t;
			}
		}

		return null;

	}

	public void createPhantomTransactions(){

		//create phantom USD deposits from external BUY records
		for(int i=0;i<this.transactionsList.size();i++){
			Transaction t = this.transactionsList.get(i);
			if(t.external && t.type == Transaction.Type.Buy){
				LOGGER.debug("Need to create phantom deposit for: {}",t);
				//generic.addTransaction(new Transaction("manual-CB-to-CBP-Transfer-1",new Date("2019-04-08T15:47:07Z"),Transaction.Type.Withdrawal,new BigNum("0.018313"),"BTC","coinbase"));
				BigDecimal total = t.quantityFrom.add(t.feeFrom);
				Long time = t.timestamp.toInstant().getEpochSecond()-5;
				
				//Transaction newt = new Transaction("phantom-"+t.externalId,new Date(t.timestamp.getTime()-1000),Transaction.Type.Deposit,total,t.assetFrom,t.exchange);
				//Transaction newt = new Transaction("phantom-"+t.externalId,LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC),Transaction.Type.Deposit,total,t.assetFrom,t.exchange);
				Transaction newt = new Transaction("phantom-"+t.externalId,ZonedDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("Z")),Transaction.Type.Deposit,total,t.assetFrom,t.exchange);
				newt.assetTo = t.assetFrom;
				newt.phantom = true;
				newt.external = true;

				LOGGER.debug("created phantom2: {}",newt);

				this.initAndAddTransaction(newt);

			}
		}

		//TODO create phantom deps and withdrawals and with for transfers with missing pairs
		for(int i=0;i<this.transactionsList.size();i++){

			Transaction t = this.transactionsList.get(i);
			
			if(this.missingTransfers.get(t.externalId) != null && t.isTransferType()) {

				//LocalDateTime.ofEpochSecond(i, i, null)

				String exchange = this.missingTransfers.get(t.externalId);
				Transaction.Type type = (t.type == Transaction.Type.Withdrawal) ? Transaction.Type.Deposit : Transaction.Type.Withdrawal;
				LOGGER.warn("Need to create phantom {} for: {}",type,t);
				Long time = (type == Transaction.Type.Withdrawal)? t.timestamp.toEpochSecond()-5000 : t.timestamp.toEpochSecond()+5000;

				Transaction newt = new Transaction("phantom-"+t.externalId,ZonedDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("Z")),type,t.quantity,t.asset,exchange);
				newt.assetTo = t.asset;
				newt.phantom = true;

				LOGGER.debug("created phantom1: {}",newt);

				this.initAndAddTransaction(newt);

			}

		}

		
	}


	public List<Transaction> getTransactionsList() {
		return transactionsList;
	}
	
	
	public static BigDecimal toBigDecimal(String s) {
		if(StringUtils.isBlank(s)) return BigDecimal.ZERO;
		else return new BigDecimal(s);
	}

}
