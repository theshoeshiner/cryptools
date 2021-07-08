package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface AllocationRepository extends BaseRepository<Allocation, Long>, ExampleFilterRepository<Allocation, Long>  {

	public Optional<Allocation> findByPortfolioAndCurrency(Portfolio p, Currency c);

	public List<Allocation> findByPortfolio(Portfolio p);

	public Page<Allocation> findByPortfolio(Portfolio p,Pageable pg);

	Long countByPortfolio(Portfolio p);

	@Query("select sum(a.percent) from #{#entityName} a where a.portfolio = ?1")
	public Optional<BigDecimal> findAllocationSumByPortfolio(Portfolio p);
}