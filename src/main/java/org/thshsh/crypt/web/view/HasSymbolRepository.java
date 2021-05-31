package org.thshsh.crypt.web.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.cryptman.HasNameRepository;

public interface HasSymbolRepository<T> extends HasNameRepository<T> {

	/*public Page<T> findByNameOrSymbolContainsIgnoreCase(String name,Pageable p);

	public Integer countByNameOrSymbolContainsIgnoreCase(String name);*/


	public Page<T> findBySymbolContainsIgnoreCase(String name,Pageable p);

	public Integer countBySymbolContainsIgnoreCase(String name);



}
