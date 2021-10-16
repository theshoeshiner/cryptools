package org.thshsh.crypt.repo;

import org.thshsh.crypt.Exchange;

public interface HasNameAndKeyRepository<T> extends HasNameRepository<T> {

	public T findByKey(String name);
	
	public Exchange findByRemoteName(String in);

}
