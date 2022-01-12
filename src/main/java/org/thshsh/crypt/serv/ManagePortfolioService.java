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

	//BigDecimal indThreshold = new BigDecimal(".15");
	//BigDecimal portThreshold = new BigDecimal(".04");
	
	@PostConstruct
	public void postConstruct() {
		template = new TransactionTemplate(transactionManager);
	}

	public PortfolioHistory createHistory(Portfolio p) {
		return createHistory(p, true);
	}
	
	public PortfolioHistory createHistory(Portfolio p, Boolean setLatest) {
		
		PortfolioHistory history = template.execute( (TransactionStatus action) -> {

			Portfolio port = portRepo.findById(p.getId()).get();

			PortfolioHistory portHistory = getSummary(port);

			if(setLatest) {
				port.setLatest(portHistory);
				portHistRepo.save(portHistory);
			}

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
		MutableInt undefinedCount = new MutableInt(0);


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
			
			BigDecimal rate = BigDecimal.ZERO;
			
			if(rates.containsKey(cur)) {
				rate = rates.get(cur).getRate();
			}
			else {
				LOGGER.warn("No market rate for: {}",cur);
			}
			
			
			//if(rates.containsKey(cur)) {
	
			BigDecimal value = bal.getAsBigDecimal().multiply(rate);
			currencyValues.put(cur, value);
			totalValue.inc(value);
			Allocation allocation = null;
			if(allocationMap.containsKey(cur)) {
				allocation = allocationMap.get(cur);
			}

			if(allocation == null || allocation.getPercent() == null) {
				undefinedCount.increment();
			}
			PortfolioEntryHistory pe = new PortfolioEntryHistory(history,bal.getAsBigDecimal(), cur,allocation,rates.get(cur));
			pe.setValue(value);
			entryMap.put(cur, pe);
			entries.add(pe);

			LOGGER.info("value: {}",value);
		
			//}
			//else {
				//LOGGER.warn("No Market rate for: {}",cur);
			//}
			
		});

		Long undefinedCountLong = undefinedCount.longValue();
		

		if(undefinedCountLong > 0) {
			BigDecimal remainderPer = remainder.divide(BigDecimal.valueOf(undefinedCountLong),4, RoundingMode.HALF_EVEN);
			LOGGER.info("{} / {} = {}", remainder,undefinedCount,remainderPer);
			
			entryMap.forEach(( currency,entry) -> {
				if(entry.getAllocationPercent() == null) {
					entry.setAllocationPercent(remainderPer);
				}
			});
		}
		else {
			//there are no undefined allocations
			if(remainder.compareTo(BigDecimal.ZERO) > 0) {
				//we have an unallocated remainder
				PortfolioEntryHistory pe = new PortfolioEntryHistory(history,BigDecimal.ZERO, null,null,null);
				pe.setAllocationPercent(remainder);
				pe.setAllocationUndefined(true);
				pe.setValue(BigDecimal.ZERO);
				entries.add(pe);
				entryMap.put(null, pe);
			}
		}


		//detect alerts

		//LOGGER.info("remainderAllocation: {}",remainderAllocation);
		LOGGER.info("currencyBalances: {}",currencyBalances.keySet());

		LOGGER.info("total value: {}",totalValue.getAsBigDecimal());

		MutableBigDecimal maxToTriggerPercent = new MutableBigDecimal(0l);
		MutableBigDecimal totalAdjust = new MutableBigDecimal(0);

		entryMap.forEach((cur,pe)-> {

			LOGGER.info("cur: {}",cur);

			//PortfolioEntryHistory pe = entryMap.get(cur);
			
			//if(pe != null) {
				
				BigDecimal percent = pe.getAllocationPercent();
				BigDecimal targetValue = totalValue.getAsBigDecimal().multiply(percent);
				LOGGER.info("targetValue: {}",targetValue);
				pe.setTargetReserve(targetValue);
				
				//BigDecimal adjPerc = pe.getAdjustReserve().divide(totalValue.getAsBigDecimal(),RoundingMode.HALF_EVEN);
				BigDecimal adjPerc = totalValue.getAsBigDecimal().compareTo(BigDecimal.ZERO) == 0?
						BigDecimal.ZERO
						:pe.getAdjustReserve().divide(totalValue.getAsBigDecimal(),RoundingMode.HALF_EVEN);
				LOGGER.info("adjPerc: {}",adjPerc);
				pe.setAdjustPercent(adjPerc);
				totalAdjust.inc(pe.getAdjustPercentAbsolute());
	
				//thresh - 15/4
				BigDecimal indThreshold = portfolio.getSettings().getIndividualThreshold();
				BigDecimal portThreshold = portfolio.getSettings().getPortfolioThreshold();
				BigDecimal thresh;
				BigDecimal adjustedIndividual = pe.getAllocationPercent().multiply(indThreshold);
				LOGGER.info("ind thresh: {}",adjustedIndividual);
				if(adjustedIndividual.compareTo(portThreshold) >0)  thresh = portThreshold;
				else thresh = adjustedIndividual;
				pe.setThresholdPercent(thresh);
	
				LOGGER.info("Threshold: {}",thresh);
	
	
				if(pe.getAllocationPercent().compareTo(BigDecimal.ZERO) != 0) {
	
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
			
			//}


		});

		history.setValue(totalValue.getAsBigDecimal());
		history.setMaxToTriggerPercent(maxToTriggerPercent.getAsBigDecimal());
		history.setTotalAdjustPercent(totalAdjust.getAsBigDecimal());

		return history;
	}

}
