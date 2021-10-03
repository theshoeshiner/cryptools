package org.thshsh.crypt.repo;

import java.util.Optional;

import org.thshsh.crypt.Role;

public interface RoleRepository extends BaseRepository<Role, Long > {
	
	public Optional<Role> findByKey(String key);

}
