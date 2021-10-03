package org.thshsh.crypt.repo;

import java.util.Optional;

import org.thshsh.crypt.Role;
import org.thshsh.crypt.web.repo.BaseRepository;

public interface RoleRepository extends BaseRepository<Role, Long > {
	
	public Optional<Role> findByKey(String key);

}
