package org.thshsh.crypt;

import java.util.Optional;

import org.thshsh.crypt.web.repo.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long > {

	Optional<User> findByEmail(String email);
	Optional<User> findByUserNameIgnoreCase(String un);
	Optional<User> findByEmailEqualsOrUserNameEquals(String email,String username);

}
