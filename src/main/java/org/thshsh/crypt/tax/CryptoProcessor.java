package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.serv.MarketRateService;

import com.helger.commons.mutable.MutableBigDecimal;

@Component
@Scope("prototype")
public class CryptoProcessor {

	public static final Logger LOGGER = LoggerFactory.getLogger(CryptoProcessor.class);
	
	@Autowired
	MarketRateService rateService;
	
	@Autowired
	CurrencyRepository currRepo;

	List<Transaction> transactions;
	Integer loading;
	Accounts accounts;
	
	List<Integer> years;

	CryptoProcessor (){
		this.loading = 0;
		this.accounts = new Accounts();
	}
	
	public void setTransactions(List<Transaction> trs) {
		this.transactions = (trs==null)?new ArrayList<>():trs;
	}
	
	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public Accounts getAccounts() {
		return accounts;
	}

	public void gainsReport() {

		GainsReport gainsReport = new GainsReport(years);
		gainsReport.addRecords(this.accounts.records);


	}

	public void balanceReport() {
		balanceReport(ZonedDateTime.now());
	}
	
	BigDecimal MIN = new BigDecimal(".0001");
	
	public void balanceReport(ZonedDateTime timestamp) {
		
		List<Asset> assets = new ArrayList<>(accounts.assetMap.values());
		
		Collections.sort(assets, (a0,a1) -> {
			return a1.getBalance().compareTo(a0.getBalance());
		});

		//BigDecimal totalValue = BigDecimal.ZERO;
		MutableBigDecimal totalValue = new MutableBigDecimal(BigDecimal.ZERO);
		
		assets.forEach(asset -> {
			BigDecimal value = BigDecimal.ZERO;
			if(asset.getBalance().compareTo(MIN)>0) {
				Currency c = currRepo.findByKeyIgnoreCase(asset.name);
				MarketRate rate = rateService.getMarketRates(timestamp, c).get(c);
				if(rate != null) {
					value = asset.getBalance().multiply(rate.getRate());
				}
				totalValue.inc(value);
				
			}
			LOGGER.info("Asset: {} Balance: {} Value: {}",asset.getName(),asset.getBalance(),value);
		});
		
		LOGGER.info("Total Value: {}",totalValue);
	}
	

    public void processTransactions() {

        LOGGER.info("processTransactions");
		List<Transaction> transactions = this.transactions;
		try {

			LOGGER.info("processing...");

			//Transaction last = null;
			for(Transaction t : transactions) {
				
				//NOTE this was to calculate what my curruent value would be if I had left my holdings static at this point in time
				/*if(last!=null && last.timestamp.getYear() != t.timestamp.getYear()) {
					LOGGER.info("New Year: {}",t.timestamp);
					balanceReport();
				}*/
				accounts.processTransaction(t);
				//last = t;
			}

			LOGGER.info("accounts: {}",this.accounts);

			accounts.assetMap.forEach((key,value) -> {
				if(value.balance.compareTo(BigDecimal.ZERO) > 0 )
				LOGGER.info("{} : {}",new Object[] {key,value.balance});
			});



		}
		finally {

		}

    }




	/*
	This initializes the transactions so that they use the BigNum class and
	maps the fiat price based on historical price data (so we know exact values when trading between crypto currencies)
	needs to be async since we need http requests
	*/

    public void initTransactions() {

		List<Transaction> ts = this.transactions;
		LOGGER.info("initTransactions");

		for (Transaction t : ts) {

			LOGGER.info("initTransaction: {}",t);

			if(t.feeTo == null) t.feeTo = BigDecimal.ZERO;
			if(t.feeFrom == null) t.feeFrom = BigDecimal.ZERO;
			
			Currency to = t.assetTo!=null?currRepo.findByKeyIgnoreCase(t.assetTo):null;
			Currency from = t.assetFrom!=null?currRepo.findByKeyIgnoreCase(t.assetFrom):null;

			if(t.isTradeType()) {

				BigDecimal spotFrom = BigDecimal.ONE;
				BigDecimal spotTo = BigDecimal.ONE;
				
				//if(from == null || to == null) throw new IllegalS

				if(t.assetFrom.equals(Asset.FIAT_ASSET)) {
					//from price is USD so its 1
					//to spot price is just the price / quantity
					LOGGER.info("quantityFrom: {} quantityTo: {}",t.quantityFrom,t.quantityTo);
					spotTo = t.quantityFrom.divide(t.quantityTo ,RoundingMode.HALF_EVEN);
				}
				else if(t.assetTo.equals(Asset.FIAT_ASSET)){
					LOGGER.info("quantityFrom: {} quantityTo: {}",t.quantityFrom,t.quantityTo);
					spotFrom = t.quantityTo.divide(t.quantityFrom,RoundingMode.HALF_EVEN);
				}
				else {
					
					Map<Currency,MarketRate> rates = rateService.getMarketRates(t.timestamp, from,to);
					
					//spotFrom = this.cryptoCompare.getHistoricalPrice(t.assetFrom, t.time);
					//spotTo = this.cryptoCompare.getHistoricalPrice(t.assetTo, t.time);
					
					t.fromRate = rates.get(from);
					t.toRate = rates.get(to);
					
					spotFrom = rates.get(from).getRate();
					spotTo = rates.get(to).getRate();
					
					
				}


				//trade between CCs

				t.fiatFrom = t.quantityFrom.multiply(spotFrom);
				t.fiatTo = t.quantityTo.multiply(spotTo);

				//calculate fees in fiat based on spot prices
				t.fiatFeeFrom = t.feeFrom.multiply(spotFrom);
				t.pricePerFrom = t.fiatFrom .divide(t.quantityFrom,RoundingMode.HALF_EVEN);
				t.fiatFeeTo = t.feeTo.multiply(spotTo);
				//if(t.fiatTo.equals(BigDecimal.ZERO)) {
				if(t.fiatTo.compareTo(BigDecimal.ZERO) == 0) {	
					//LOGGER.info("fiatTo is zero: {}",t.fiatTo);
					t.pricePerTo = BigDecimal.ZERO;
				}
				else {
					//LOGGER.info("fiatTo is nonzero: {}",t.fiatTo);
					t.pricePerTo = t.fiatTo.divide(t.quantityTo,RoundingMode.HALF_EVEN);
				}
				//t.pricePerTo = t.fiatTo.equals(BigDecimal.ZERO)?{BigDecimal.ZERO: t.fiatTo.divide(t.quantityTo,RoundingMode.HALF_EVEN);

			}
			else if(t.isIncomeType()){
				//for income we need all the same stuff as if we traded to USD, and we will use the fair market value as the cost basis
				//BigDecimal spotTo = this.cryptoCompare.getHistoricalPrice(t.assetTo, t.time);
				Map<Currency,MarketRate> rates = rateService.getMarketRates(t.timestamp, to);
				BigDecimal spotTo = rates.get(to).getRate();
				
				t.fiatTo = t.quantityTo.multiply(spotTo);
				t.fiatFeeTo = t.feeTo.multiply(spotTo);
				t.pricePerTo = t.fiatTo.divide(t.quantityTo,RoundingMode.HALF_EVEN);
			}
			else {
				//don't need to init send or recieves
			}

			LOGGER.info("inited Transaction: {}",t);
		}

		LOGGER.info("Init'd Transactions...");


	}



}