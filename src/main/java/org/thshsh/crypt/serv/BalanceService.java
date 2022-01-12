package org.thshsh.crypt.serv;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.AppSession;

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
		
		/*BigDecimal setAllocation = entry.getAllocationUndefined()?BigDecimal.ZERO:entry.getAllocationPercent();
		LOGGER.debug("setAllocation: {}",setAllocation);
		
		BigDecimal changeAllocation = actualAllocation.subtract(setAllocation);
		LOGGER.debug("changeAllocation: {}",changeAllocation);*/
		
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
		
		//LOGGER.debug("addBalance: {}",addBalance);
		//BigDecimal addBalanceValue = addBalance.multiply(rate.getRate());
		//LOGGER.debug("balanceValue: {}",addBalanceValue);
		//BigDecimal newValue = currentValue.add(addBalanceValue);
		//LOGGER.debug("newValue: {}",newValue);
		
		/*BigDecimal reduce = newValue.compareTo(BigDecimal.ZERO) == 0? BigDecimal.ZERO: currentValue.divide(newValue, RoundingMode.HALF_EVEN);
		LOGGER.debug("reduce: {}",reduce);
		
		BigDecimal totalBalance = addBalance.add(latest.getBalance(c).orElse(BigDecimal.ZERO));
		LOGGER.debug("totalBalance: {}",totalBalance);
		BigDecimal totalBalanceValue = totalBalance.multiply(rate.getRate());
		LOGGER.debug("totalBalanceValue: {}",totalBalanceValue);
		
		BigDecimal actualAllocation =
				newValue.compareTo(BigDecimal.ZERO) == 0? BigDecimal.ZERO: 
					totalBalanceValue.divide(newValue, RoundingMode.HALF_EVEN);
				//totalBalanceValue.divide(newValue,RoundingMode.HALF_EVEN);
		actualAllocation = actualAllocation.setScale(3, RoundingMode.DOWN);
		LOGGER.debug("actualAllocation: {}",actualAllocation);
		
		
		portfolio.getAllocations().forEach(allo -> {
			LOGGER.info("allocation: {}",allo);
		});
		
		MutableBigDecimal totalAllocated = new MutableBigDecimal(0);
		
		portfolio.getAllocations().stream().filter(a -> !a.isUndefined() && !a.getCurrency().equals(c)).forEach(allo -> {
			LOGGER.info("reduce allo: {}",allo);
			BigDecimal newAllo = allo.getPercent().multiply(reduce);
			allo.setPercent(newAllo);
			totalAllocated.inc(newAllo);
		});
		
		LOGGER.info("total allocated {} ",totalAllocated);*/
		
		/*portfolio.getAllocations().forEach(allo -> {
			LOGGER.info("allo: {}",allo);
			if(!allo.isUndefined()) {
				BigDecimal newAllo = allo.getPercent().multiply(reduce);
				allo.setPercent(newAllo);
			}
		});*/
		
		
		
		/*	Allocation a = portfolio.getAllocation(c).orElse(new Allocation(portfolio,c));
			a.setPercent(actualAllocation);
			a.setUndefined(false);
			alloRepo.save(a);*/

	}

	/*@Transactional
	public void autoDetectAllocation(Balance entity,Portfolio port) {
		
		//Boolean edit = entity.getId()!=null;
		Portfolio portfolio = portRepo.getById(port.getId());
		BigDecimal oldBalance = entity.getId()==null?BigDecimal.ZERO:portfolio.getBalances().stream().filter(b -> b.getId().equals(entity.getId())).findFirst().get().getBalance();
		LOGGER.info("old balance: {}",oldBalance);
	
		Currency c = entity.getCurrency();
		PortfolioHistory latest = portfolio.getLatest();
		BigDecimal currentValue = latest.getValue();
		
		
		
		LOGGER.debug("currentValue: {}",currentValue);
		MarketRate rate = rateService.getUpToDateMarketRate(AppSession.getCurrentUser().getApiKey(), c);
		
		LOGGER.debug("balance: {}",entity.getBalance());
		BigDecimal addBalance = entity.getBalance().subtract(oldBalance);
		LOGGER.debug("addBalance: {}",addBalance);
		BigDecimal addBalanceValue = addBalance.multiply(rate.getRate());
		LOGGER.debug("balanceValue: {}",addBalanceValue);
		BigDecimal newValue = currentValue.add(addBalanceValue);
		LOGGER.debug("newValue: {}",newValue);
		BigDecimal reduce = newValue.compareTo(BigDecimal.ZERO) == 0? BigDecimal.ZERO: currentValue.divide(newValue, RoundingMode.HALF_EVEN);
		LOGGER.debug("reduce: {}",reduce);
		
		BigDecimal totalBalance = addBalance.add(latest.getBalance(c).orElse(BigDecimal.ZERO));
		LOGGER.debug("totalBalance: {}",totalBalance);
		BigDecimal totalBalanceValue = totalBalance.multiply(rate.getRate());
		LOGGER.debug("totalBalanceValue: {}",totalBalanceValue);
	
		BigDecimal actualAllocation =
				newValue.compareTo(BigDecimal.ZERO) == 0? BigDecimal.ZERO: 
					totalBalanceValue.divide(newValue, RoundingMode.HALF_EVEN);
				//totalBalanceValue.divide(newValue,RoundingMode.HALF_EVEN);
		actualAllocation = actualAllocation.setScale(3, RoundingMode.DOWN);
		LOGGER.debug("actualAllocation: {}",actualAllocation);
		
		
		portfolio.getAllocations().forEach(allo -> {
			LOGGER.info("allocation: {}",allo);
		});
		
		MutableBigDecimal totalAllocated = new MutableBigDecimal(0);
	
		portfolio.getAllocations().stream().filter(a -> !a.isUndefined() && !a.getCurrency().equals(c)).forEach(allo -> {
			LOGGER.info("reduce allo: {}",allo);
			BigDecimal newAllo = allo.getPercent().multiply(reduce);
			allo.setPercent(newAllo);
			totalAllocated.inc(newAllo);
		});
		
		LOGGER.info("total allocated {} ",totalAllocated);
	
		
		Allocation a = portfolio.getAllocation(c).orElse(new Allocation(portfolio,c));
		a.setPercent(actualAllocation);
		a.setUndefined(false);
		alloRepo.save(a);
	
		
	}*/

}
