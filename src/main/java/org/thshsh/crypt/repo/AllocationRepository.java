package org.thshsh.crypt.repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;

public interface AllocationRepository extends BaseRepository<Allocation, Long>  {

	public Optional<Allocation> findByPortfolioAndCurrency(Portfolio p, Currency c);

	public List<Allocation> findByPortfolio(Portfolio p);

	public Page<Allocation> findByPortfolio(Portfolio p,Pageable pg);

	Long countByPortfolio(Portfolio p);

	@Query("select sum(a.percent) from #{#entityName} a where a.portfolio = ?1")
	public Optional<BigDecimal> findAllocationSumByPortfolio(Portfolio p);
	
	@Query("select a.currency.key from #{#entityName} a where a.portfolio = ?1 order by a.percent desc NULLS LAST")
	public List<String> findCurrencySymbols(Portfolio p);
	
	@Query("select a.currency.key from #{#entityName} a where a.portfolio = ?1 order by a.percent desc NULLS LAST")
	public List<String> findExchangeNames(Portfolio p);
}
