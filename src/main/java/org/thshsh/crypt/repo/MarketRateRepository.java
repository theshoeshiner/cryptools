package org.thshsh.crypt.repo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;

public interface MarketRateRepository extends BaseRepository<MarketRate, Long> {

	//@Query("select mr from MarketRate mr group by")
	//Stream<MarketRate> findByCurrencyInOrderByTimestampDesc(Collection<Currency> c);

	MarketRate findTopByCurrencyOrderByTimestampDesc(Currency c);

}
