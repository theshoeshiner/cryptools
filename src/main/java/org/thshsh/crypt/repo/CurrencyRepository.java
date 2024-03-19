package org.thshsh.crypt.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.Currency;

public interface CurrencyRepository extends BaseRepository<Currency, Long>, HasSymbolRepository<Currency>  {

	@Query("select t from #{#entityName} t where ( lower(t.key) like %?1% or lower(t.name) like %?1% ) and ( t.platformType <> ?#{T(org.thshsh.crypt.PlatformType).derivative} or t.platformType is null)")
	public Page<Currency> findByString(String name,Pageable p);

	@Query("select count(t) from #{#entityName} t where ( lower(t.key) like %?1% or lower(t.name) like %?1% ) and ( t.platformType <> ?#{T(org.thshsh.crypt.PlatformType).derivative} or t.platformType is null)")
	public Integer countByString(String name);
	
	@Query("select c from Currency c where c.platformType <> ?#{T(org.thshsh.crypt.PlatformType).fiat}")
	public List<Currency> findAllNotFiat();

	public List<Currency> findByRemoteIdNotNull();
}
