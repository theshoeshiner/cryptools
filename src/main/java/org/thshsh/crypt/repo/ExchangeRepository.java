package org.thshsh.crypt.repo;

import org.thshsh.crypt.Exchange;
import org.thshsh.vaadin.data.QueryByExampleRepository;

public interface ExchangeRepository extends BaseRepository<Exchange, Long>, HasNameAndKeyRepository<Exchange>, QueryByExampleRepository<Exchange, Long> {

	
	
}
