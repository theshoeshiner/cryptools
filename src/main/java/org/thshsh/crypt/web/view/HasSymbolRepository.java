package org.thshsh.crypt.web.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.repo.HasNameAndKeyRepository;

public interface HasSymbolRepository<T> extends HasNameAndKeyRepository<T> {

	/*public Page<T> findByNameOrSymbolContainsIgnoreCase(String name,Pageable p);

	public Integer countByNameOrSymbolContainsIgnoreCase(String name);*/

	public T findByKeyIgnoreCase(String key);

	public Page<T> findByKeyContainsIgnoreCase(String name,Pageable p);

	public Integer countByKeyContainsIgnoreCase(String name);

	//@Query("select t from #{#entityName} t where (lower(t.symbol) like %?1% or lower(t.name) like %?1%) and t.platformType in (?#{T(org.thshsh.crypt.PlatformType).blockchain},?#{T(org.thshsh.crypt.PlatformType).fiat} )")
	public Page<T> findByKeyContainsIgnoreCaseOrNameContainsIgnoreCase(String sym,String name,Pageable p);

	

	//@Query("select count(t) from #{#entityName} t where (lower(t.symbol) like %?1% or lower(t.name) like %?1%) and t.platformType in (?#{T(org.thshsh.crypt.PlatformType).blockchain},?#{T(org.thshsh.crypt.PlatformType).fiat} )")
	public Integer countByKeyContainsIgnoreCaseOrNameContainsIgnoreCase(String sym,String name);

}
