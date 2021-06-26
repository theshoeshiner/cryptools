package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.web.view.PortfolioEntry;

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
	MarketRateService rateService;

	BigDecimal indThreshold = new BigDecimal(".15");
	BigDecimal portThreshold = new BigDecimal(".04");

	public PortfolioSummary getSummary(Portfolio portfolio) {

		PortfolioSummary summary = new PortfolioSummary();

		List<PortfolioEntry> entries = new ArrayList<PortfolioEntry>();
		summary.entries = entries;

		BigDecimal sum = alloRepo.findAllocationSumByPortfolio(portfolio);

		BigDecimal remainder = BigDecimal.ONE.subtract(sum);
		LOGGER.info("remainder: {}",remainder);
		Allocation remainderAllocation = new Allocation();
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

		Map<Currency,MarketRate> rates = rateService.getUpToDateMarketRates(currencyBalances.keySet());
		LOGGER.info("got rates: {}",rates);

		Map<Currency,BigDecimal> currencyValues = new HashMap<>();
		Map<Currency,PortfolioEntry> entryMap = new HashMap<>();

		MutableBigDecimal totalValue = new MutableBigDecimal(0);


		currencyBalances.forEach((cur,bal)-> {
			BigDecimal value = bal.getAsBigDecimal().multiply(rates.get(cur).getRate());
			currencyValues.put(cur, value);
			totalValue.inc(value);
			Allocation a;
			if(allocationMap.containsKey(cur)) {
				a = allocationMap.get(cur);
			}
			else {
				a = remainderAllocation;
				remainderCount.increment();
			}
			PortfolioEntry pe = new PortfolioEntry(bal.getAsBigDecimal(), cur,a,rates.get(cur));
			pe.setValueReserve(value);
			entryMap.put(cur, pe);
			entries.add(pe);
		});

		BigDecimal remainderPer = remainder.divide(BigDecimal.valueOf(remainderCount.longValue()),4, RoundingMode.HALF_EVEN);
		LOGGER.info("{} / {} = {}", remainder,remainderCount,remainderPer);
		remainderAllocation.setPercent(remainderPer);

		LOGGER.info("remainderAllocation: {}",remainderAllocation);
		LOGGER.info("currencyBalances: {}",currencyBalances.keySet());

		LOGGER.info("total value: {}",totalValue.getAsBigDecimal());

		MutableBigDecimal maxToTriggerPercent = new MutableBigDecimal(0l);
		MutableBigDecimal totalAdjust = new MutableBigDecimal(0);

		currencyBalances.forEach((cur,bal)-> {

			LOGGER.info("cur: {}",cur);

			PortfolioEntry pe = entryMap.get(cur);
			BigDecimal percent = pe.getAllocation().getPercent();
			BigDecimal targetValue = totalValue.getAsBigDecimal().multiply(percent);
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



			if(!pe.getAllocation().equals(remainderAllocation)) {
				BigDecimal toTrigger = adjPerc.divide(thresh, RoundingMode.HALF_EVEN).abs();
				LOGGER.info("toTrigger: {}",toTrigger);
				pe.setToTriggerPercent(toTrigger);
				if(toTrigger.compareTo(maxToTriggerPercent.getAsBigDecimal()) >0) {
					maxToTriggerPercent.set(toTrigger);
				}
			}

		});

		summary.totalValue = totalValue.getAsBigDecimal();
		summary.setMaxToTriggerPercent(maxToTriggerPercent.getAsBigDecimal());
		summary.totalAdjustPercent = totalAdjust.getAsBigDecimal();

		return summary;
	}

}
