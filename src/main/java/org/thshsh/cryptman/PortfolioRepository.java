package org.thshsh.cryptman;

import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface PortfolioRepository  extends BaseRepository<Portfolio, Long>, ExampleFilterRepository<Portfolio, Long>  {

}
