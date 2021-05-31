package org.thshsh.crypt;

import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.crypt.web.view.HasSymbolRepository;
import org.thshsh.cryptman.HasNameRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface CurrencyRepository extends BaseRepository<Currency, Long>, ExampleFilterRepository<Currency, Long>, HasSymbolRepository<Currency>  {

	public Currency findBySymbol(String s);

}
