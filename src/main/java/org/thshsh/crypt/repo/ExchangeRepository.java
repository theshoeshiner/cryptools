package org.thshsh.crypt.repo;

import org.thshsh.crypt.Exchange;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface ExchangeRepository extends BaseRepository<Exchange, Long>, HasNameAndKeyRepository<Exchange>, ExampleFilterRepository<Exchange, Long> {

	
	
}
