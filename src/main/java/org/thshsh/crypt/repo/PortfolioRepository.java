package org.thshsh.crypt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.crypt.User;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface PortfolioRepository  extends BaseRepository<Portfolio, Long>, ExampleFilterRepository<Portfolio, Long>  {

	public Page<Portfolio> findByUser(User user,Pageable p);

	public Long countByUser(User user);

}
