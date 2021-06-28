package org.thshsh.cryptman;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface BalanceRepository extends BaseRepository<Balance, Long>, ExampleFilterRepository<Balance, Long>  {

	List<Balance> findByPortfolio(Portfolio p);

	Page<Balance> findByPortfolio(Portfolio p,Pageable page);

	Long countByPortfolio(Portfolio p);

}
