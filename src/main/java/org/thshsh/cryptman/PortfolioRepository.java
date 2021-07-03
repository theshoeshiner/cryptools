package org.thshsh.cryptman;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface PortfolioRepository  extends BaseRepository<Portfolio, Long>, ExampleFilterRepository<Portfolio, Long>  {

	public Page<Portfolio> findByUser(User user,Pageable p);

	public Long countByUser(User user);

}
