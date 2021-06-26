package org.thshsh.cryptman;

import java.util.List;

import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface BalanceRepository extends BaseRepository<Balance, Long>, ExampleFilterRepository<Balance, Long>  {

	List<Balance> findByPortfolio(Portfolio p);

}
