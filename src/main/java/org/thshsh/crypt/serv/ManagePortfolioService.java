package org.thshsh.crypt.serv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.repo.PortfolioRepository;

import com.helger.commons.mutable.MutableBigDecimal;

@Service
public class ManagePortfolioService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ManagePortfolioService.class);

	@Autowired
	ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;

	@Autowired
	AllocationRepository alloRepo;
	
	@Autowired
	PortfolioHistoryRepository portHistRepo;
	
	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	MarketRateService rateService;
	

	@Autowired
	PlatformTransactionManager transactionManager;
	
	TransactionTemplate template;

	BigDecimal indThreshold = new BigDecimal(".15");
	BigDecimal portThreshold = new BigDecimal(".04");
	
	@PostConstruct
	public void postConstruct() {
		template = new TransactionTemplate(transactionManager);
	}

	public PortfolioHistory createHistory(Portfolio p) {
		
		PortfolioHistory history = template.execute( (TransactionStatus action) -> {

			//PortfolioHistory mostRecent = portHistRepo.findOneByPortfolioOrderByTimestampDesc(p);

			Portfolio port = portRepo.findById(p.getId()).get();

			PortfolioHistory portHistory = getSummary(port);
			//PortfolioHistory portHistory = new PortfolioHistory(port, summary);
			//portHistory.setValue(portHistory.getValue());
			/*summary.getEntries().forEach(entry -> {
				PortfolioEntryHistory hist = new PortfolioEntryHistory(portHistory, entry);
				portHistory.addEntry(hist);
			});*/
			port.setLatest(portHistory);
			portHistRepo.save(portHistory);

			return portHistory;
		});
		
		return history;

		
	}
	
	protected PortfolioHistory getSummary(Portfolio portfolio) {
		
		LOGGER.info("getSummary: {}",portfolio);

		PortfolioHistory history = new PortfolioHistory(portfolio);

		Set<PortfolioEntryHistory> entries = new HashSet<PortfolioEntryHistory>();
		history.setEntries(entries);

		BigDecimal sum = alloRepo.findAllocationSumByPortfolio(portfolio).orElse(BigDecimal.ZERO);

		BigDecimal remainder = BigDecimal.ONE.subtract(sum);
		LOGGER.info("remainder: {}",remainder);
		//Allocation remainderAllocation = new Allocation();
		MutableInt remainderCount = new MutableInt(0);


		List<Allocation> allocations = alloRepo.findByPortfolio(portfolio);
		Map<Currency,Allocation> allocationMap = allocations.stream().collect(Collectors.toMap(Allocation::getCurrency, Function.identity()));

		Map<Currency,MutableBigDecimal> currencyBalances = new HashMap<>();
		balanceRepo.findByPortfolio(portfolio).forEach(bal -> {
			LOGGER.info("Balance: {}",bal);
			if(!currencyBalances.containsKey(bal.getCurrency())) currencyBalances.put(bal.getCurrency(), new MutableBigDecimal(0));
			currencyBalances.get(bal.getCurrency()).inc(bal.getBalance());
		});

		LOGGER.info("currencyBalances: {}",currencyBalances.keySet());

		Map<Currency,MarketRate> rates = rateService.getUpToDateMarketRates(portfolio.getUser().getApiKey(),currencyBalances.keySet());
		LOGGER.info("got rates: {}",rates);

		Map<Currency,BigDecimal> currencyValues = new HashMap<>();
		Map<Currency,PortfolioEntryHistory> entryMap = new HashMap<>();

		MutableBigDecimal totalValue = new MutableBigDecimal(0);


		currencyBalances.forEach((cur,bal)-> {

			LOGGER.info("cur: {}",cur);
			
			BigDecimal rate;
			
			if(rates.containsKey(cur)) {
				rate = rates.get(cur).getRate();
			
	
				BigDecimal value = bal.getAsBigDecimal().multiply(rate);
				currencyValues.put(cur, value);
				totalValue.inc(value);
				Allocation allocation = null;
				if(allocationMap.containsKey(cur)) {
					allocation = allocationMap.get(cur);
				}
	
				if(allocation == null || allocation.getPercent() == null) {
					remainderCount.increment();
				}
				PortfolioEntryHistory pe = new PortfolioEntryHistory(history,bal.getAsBigDecimal(), cur,allocation,rates.get(cur));
				pe.setValueReserve(value);
				entryMap.put(cur, pe);
				entries.add(pe);
	
				LOGGER.info("value: {}",value);
			
			}
			else {
				LOGGER.warn("No Market rate for: {}",cur);
			}
			
		});

		Long count = remainderCount.longValue();
		if(count == 0) count++;
		BigDecimal remainderPer = remainder.divide(BigDecimal.valueOf(count),4, RoundingMode.HALF_EVEN);
		LOGGER.info("{} / {} = {}", remainder,remainderCount,remainderPer);
		//remainderAllocation.setPercent(remainderPer);

		
		
		entryMap.forEach(( currency,entry) -> {
			
			if(entry.getAllocation() == null || entry.getAllocation().getPercent() == null) {
				//Allocation a = new Allocation(remainderPer);
				Allocation temp = new Allocation();
				temp.setPercent(remainderPer);
				temp.setUndefined(true);
				temp.setCurrency(currency);
				temp.setPortfolio(portfolio);
				if(entry.getAllocation()!=null)temp.setId(entry.getAllocation().getId());
				entry.setAllocation(temp);
				
			}
			//else if(entry.getAllocation().getPercent() == null) {
				//temp.setUndefined(true);
				//temp.setPercent(remainderPer);
				
			//	temp.setId(entry.getAllocation().getId());
			//}
			
		});



		//LOGGER.info("remainderAllocation: {}",remainderAllocation);
		LOGGER.info("currencyBalances: {}",currencyBalances.keySet());

		LOGGER.info("total value: {}",totalValue.getAsBigDecimal());

		MutableBigDecimal maxToTriggerPercent = new MutableBigDecimal(0l);
		MutableBigDecimal totalAdjust = new MutableBigDecimal(0);

		currencyBalances.forEach((cur,bal)-> {

			LOGGER.info("cur: {}",cur);

			PortfolioEntryHistory pe = entryMap.get(cur);
			
			if(pe != null) {
				
				BigDecimal percent = pe.getAllocation().getPercent();
				BigDecimal targetValue = totalValue.getAsBigDecimal().multiply(percent);
				LOGGER.info("targetValue: {}",targetValue);
				pe.setTargetReserve(targetValue);
	
				BigDecimal adjPerc = pe.getAdjustReserve().divide(totalValue.getAsBigDecimal(),RoundingMode.HALF_EVEN);
				LOGGER.info("adjPerc: {}",adjPerc);
				pe.setAdjustPercent(adjPerc);
				totalAdjust.inc(pe.getAdjustPercentAbsolute());
	
				//thresh - 15/4
				BigDecimal thresh;
				BigDecimal ind = pe.getAllocation().getPercent().multiply(indThreshold);
				LOGGER.info("ind thresh: {}",ind);
				if(ind.compareTo(portThreshold) >0)  thresh = portThreshold;
				else thresh = ind;
				pe.setThresholdPercent(thresh);
	
				LOGGER.info("Threshold: {}",thresh);
	
	
				if(pe.getAllocation().getPercent().compareTo(BigDecimal.ZERO) != 0) {
	
					BigDecimal toTrigger = adjPerc.divide(thresh, RoundingMode.HALF_EVEN).abs();
					LOGGER.info("toTrigger: {}",toTrigger);
	
	
					if(portfolio.getSettings().getMinimumAdjust() != null) {
						if(pe.getAdjustAbsolute().compareTo(portfolio.getSettings().getMinimumAdjust()) < 0) {
							//adjust is less than minimum so change our to trigger
							BigDecimal toMinTrigger = pe.getAdjustAbsolute().divide(portfolio.getSettings().getMinimumAdjust(),RoundingMode.HALF_EVEN);
							if(toMinTrigger.compareTo(toTrigger) < 0) {
								toTrigger = toMinTrigger;
							}
						}
					}
	
	
					pe.setToTriggerPercent(toTrigger);
					if(toTrigger.compareTo(maxToTriggerPercent.getAsBigDecimal()) >0) {
						maxToTriggerPercent.set(toTrigger);
					}
	
				}
			
			}


		});

		history.setValue(totalValue.getAsBigDecimal());
		history.setMaxToTriggerPercent(maxToTriggerPercent.getAsBigDecimal());
		history.setTotalAdjustPercent(totalAdjust.getAsBigDecimal());

		return history;
	}

}
