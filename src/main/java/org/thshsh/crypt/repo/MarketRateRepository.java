package org.thshsh.crypt.repo;

import java.time.ZonedDateTime;
import java.util.List;

import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.vaadin.data.QueryByExampleRepository;

public interface MarketRateRepository extends BaseRepository<MarketRate, Long>, QueryByExampleRepository<MarketRate, Long> {

	//@Query("select mr from MarketRate mr group by")
	//Stream<MarketRate> findByCurrencyInOrderByTimestampDesc(Collection<Currency> c);

	MarketRate findTopByCurrencyOrderByTimestampDesc(Currency c);
	
	List<MarketRate> findByCurrencyAndTimestampGreaterThanAndTimestampLessThanOrderByTimestampDesc(Currency c,ZonedDateTime min, ZonedDateTime max);

}
