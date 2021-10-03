package org.thshsh.crypt.repo;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioHistory;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface PortfolioHistoryRepository  extends BaseRepository<PortfolioHistory, Long>, ExampleFilterRepository<PortfolioHistory, Long>  {

	List<PortfolioHistory> findByPortfolioOrderByTimestampAsc(Portfolio p);
	
	List<PortfolioHistory> findByPortfolioAndTimestampGreaterThanOrderByTimestampAsc(Portfolio p,ZonedDateTime ts);

	PortfolioHistory findOneByPortfolioOrderByTimestampDesc(Portfolio p);



	@Modifying
	@Query("delete from PortfolioHistory h where h.portfolio = ?1")
	void deleteAllByPortfolio(Portfolio p);
}
