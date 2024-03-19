package org.thshsh.crypt.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;

public interface BalanceRepository extends BaseRepository<Balance, Long> {

	List<Balance> findByPortfolio(Portfolio p);
	
	List<Balance> findByPortfolioAndCurrency(Portfolio p,Currency c);

	Page<Balance> findByPortfolio(Portfolio p,Pageable page);

	Long countByPortfolio(Portfolio p);

}
