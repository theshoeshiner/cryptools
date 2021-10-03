package org.thshsh.crypt.repo;

import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.cryptman.HasNameRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface ExchangeRepository extends BaseRepository<Exchange, Long>, HasNameRepository<Exchange>, ExampleFilterRepository<Exchange, Long> {

	
	
}
