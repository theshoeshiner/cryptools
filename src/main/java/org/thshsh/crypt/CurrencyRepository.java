package org.thshsh.crypt;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.crypt.web.view.HasSymbolRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface CurrencyRepository extends BaseRepository<Currency, Long>, ExampleFilterRepository<Currency, Long>, HasSymbolRepository<Currency>  {


	@Query("select c from Currency c where c.platformType <> ?#{T(org.thshsh.crypt.PlatformType).fiat}")
	public List<Currency> findAllNotFiat();

	public List<Currency> findByRemoteIdNotNull();
}
