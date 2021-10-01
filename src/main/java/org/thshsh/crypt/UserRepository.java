package org.thshsh.crypt;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.web.repo.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long > {

	Optional<User> findByConfirmToken(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findByUserNameIgnoreCase(String un);
	Optional<User> findByEmailEqualsOrUserNameEquals(String email,String username);
	
	@Query("select u from User u where ( u.email = ?1 or u.userName =?1) and u.confirmed = true")
	Optional<User> findByLogin(String login);

}
