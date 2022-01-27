package org.thshsh.crypt.serv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.repo.PortfolioRepository;

import com.helger.commons.mutable.MutableBigDecimal;


@Service
public class BalanceService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(BalanceService.class);
	
	@Autowired
	PortfolioHistoryRepository histRepo;
	
	@Autowired
	PortfolioRepository portRepo;
	
	@Autowired
	AllocationRepository alloRepo;
	
	@Autowired
	MarketRateService rateService;
	
	@Transactional
	public void autoDetectAllocation(Currency c, Portfolio port) {
		Portfolio portfolio = portRepo.getById(port.getId());
		autoDetectAllocation(c, port, portfolio.getLatest());
	}

	@Transactional
	public void autoDetectAllocations(Portfolio port,PortfolioHistory latest) {
		
		Portfolio portfolio = portRepo.getById(port.getId());
		BigDecimal currentValue = latest.getValue();
		
		Map<Currency,Allocation> alloMap = portfolio.getAllocations().stream().collect(Collectors.toMap(Allocation::getCurrency, Function.identity()));
		
		latest.getEntries().forEach(entry -> {
			Currency c = entry.getCurrency();
			if(c != null) {
				Allocation allo = alloMap.get(c);
				BigDecimal value = entry.getValue();
				if(allo == null) {
					allo = new Allocation(portfolio,c);
				}	
				BigDecimal actualAllocation = value.divide(currentValue, 3,RoundingMode.HALF_EVEN);
				allo.setPercent(actualAllocation);
				allo.setUndefined(false);
				alloRepo.save(allo);
			}
		});
		
	}
	
	@Transactional
	public void autoDetectAllocation(Currency c, Portfolio port,PortfolioHistory latest) {
		
		
		
		Portfolio portfolio = portRepo.getById(port.getId());
		Boolean hasUndefined = portfolio.getAllocations()
				.stream()
				.filter(a -> !a.getCurrency().equals(c))
				.filter(a -> a.isUndefined()).findAny().isPresent();

		LOGGER.info("hasUndefined: {}",hasUndefined);
		
		BigDecimal currentValue = latest.getValue();
		PortfolioEntryHistory entry = latest.getEntry(c).get();
		
		
		LOGGER.info("entry: {}",entry);
		LOGGER.debug("currentValue: {}",currentValue);
		
		if(currentValue.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

	
		BigDecimal balanceValue = entry.getValue();
		LOGGER.debug("balanceValue: {}",balanceValue);
		
		BigDecimal actualAllocation = balanceValue.divide(currentValue, 3,RoundingMode.HALF_EVEN);
		
		
		LOGGER.debug("actualAllocation: {}",actualAllocation);

		
		MutableBigDecimal totalAllocated = new MutableBigDecimal(0);
		latest.getEntries().stream()
		.filter(e -> e.getCurrency()!=null)
		.filter(e -> !Objects.equals(e.getCurrency(), c))
		.filter(e -> !e.getAllocationUndefined())
		.map(e -> e.getAllocationPercent())
		//.filter(p -> p != null)
		.forEach(p -> {
			LOGGER.info("adding allocation: {}",p);
			totalAllocated.inc(p);
		});
		LOGGER.debug("totalAllocated: {}",totalAllocated);
		
		BigDecimal remainder = BigDecimal.ONE.subtract(totalAllocated.getAsBigDecimal());
		
		LOGGER.debug("remainder: {}",remainder);
		
		
		if(remainder.compareTo(actualAllocation) > 0) {
			//just set the new allocation, dont reduce anything
			
		}
		else {
			MutableBigDecimal reducedSum = new MutableBigDecimal(0);
			
			LOGGER.info("reducing allocations to make room");
			BigDecimal reduceBy = actualAllocation.subtract(remainder);
			LOGGER.info("reduceBy: {}",reduceBy);
			BigDecimal reduceByPercent = BigDecimal.ONE.subtract(reduceBy.divide(totalAllocated.getAsBigDecimal(),RoundingMode.HALF_EVEN));
			LOGGER.info("reduceByPercent: {}",reduceByPercent);
			
			portfolio.getAllocations()
				.stream().filter(a -> !a.isUndefined() && !a.getCurrency().equals(c))
				.forEach(allo -> {
					BigDecimal newAllo = allo.getPercent().multiply(reduceByPercent).setScale(3,RoundingMode.DOWN);
					LOGGER.info("set: {} to: {}",allo.getPercent(),newAllo);
					if(!hasUndefined) reducedSum.inc(newAllo);
					allo.setPercent(newAllo);
			});
			
			if(!hasUndefined) {
				BigDecimal roundUp = BigDecimal.ONE.subtract(reducedSum.getAsBigDecimal());
				LOGGER.info("Rounding allocation from: {} to: {}",actualAllocation,roundUp);
				actualAllocation = roundUp;
			}
		}
		
		LOGGER.info("setting allocation to actual");
		Allocation a = portfolio.getAllocation(c).orElse(new Allocation(portfolio,c));
		a.setPercent(actualAllocation);
		a.setUndefined(false);
		alloRepo.save(a);
		
	

	}


}
