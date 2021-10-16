package org.thshsh.crypt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HasNameRepository<T> {
	
	public Page<T> findByNameContainsIgnoreCase(String name,Pageable p);

	public Integer countByNameContainsIgnoreCase(String name);

}
