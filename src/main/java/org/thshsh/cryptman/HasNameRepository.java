package org.thshsh.cryptman;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.crypt.Exchange;

public interface HasNameRepository<T> {

	public Page<T> findByNameContainsIgnoreCase(String name,Pageable p);

	public Integer countByNameContainsIgnoreCase(String name);

	public T findByKey(String name);
	
	public Exchange findByRemoteName(String in);

}
