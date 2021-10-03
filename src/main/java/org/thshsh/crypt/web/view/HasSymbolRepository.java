package org.thshsh.crypt.web.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.repo.HasNameRepository;

public interface HasSymbolRepository<T> extends HasNameRepository<T> {

	/*public Page<T> findByNameOrSymbolContainsIgnoreCase(String name,Pageable p);

	public Integer countByNameOrSymbolContainsIgnoreCase(String name);*/

	public T findByKeyIgnoreCase(String key);

	public Page<T> findByKeyContainsIgnoreCase(String name,Pageable p);

	public Integer countByKeyContainsIgnoreCase(String name);

	//@Query("select t from #{#entityName} t where (lower(t.symbol) like %?1% or lower(t.name) like %?1%) and t.platformType in (?#{T(org.thshsh.crypt.PlatformType).blockchain},?#{T(org.thshsh.crypt.PlatformType).fiat} )")
	public Page<T> findByKeyContainsIgnoreCaseOrNameContainsIgnoreCase(String sym,String name,Pageable p);

	@Query("select t from #{#entityName} t where ( lower(t.key) like %?1% or lower(t.name) like %?1% ) and ( t.platformType <> ?#{T(org.thshsh.crypt.PlatformType).derivative} or t.platformType is null)")
	public Page<T> findByString(String name,Pageable p);

	@Query("select count(t) from #{#entityName} t where ( lower(t.key) like %?1% or lower(t.name) like %?1% ) and ( t.platformType <> ?#{T(org.thshsh.crypt.PlatformType).derivative} or t.platformType is null)")
	public Integer countByString(String name);

	//@Query("select count(t) from #{#entityName} t where (lower(t.symbol) like %?1% or lower(t.name) like %?1%) and t.platformType in (?#{T(org.thshsh.crypt.PlatformType).blockchain},?#{T(org.thshsh.crypt.PlatformType).fiat} )")
	public Integer countByKeyContainsIgnoreCaseOrNameContainsIgnoreCase(String sym,String name);

}
