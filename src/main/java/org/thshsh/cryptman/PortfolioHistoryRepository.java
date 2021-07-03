package org.thshsh.cryptman;

import java.util.List;

import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface PortfolioHistoryRepository  extends BaseRepository<PortfolioHistory, Long>, ExampleFilterRepository<PortfolioHistory, Long>  {

	List<PortfolioHistory> findByPortfolioOrderByTimestampAsc(Portfolio p);

	PortfolioHistory findOneByPortfolioOrderByTimestampDesc(Portfolio p);

}
