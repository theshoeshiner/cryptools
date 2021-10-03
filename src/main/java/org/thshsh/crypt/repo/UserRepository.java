package org.thshsh.crypt.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.repo.BaseRepository;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface UserRepository extends BaseRepository<User, Long >, ExampleFilterRepository<User, Long> {

	Optional<User> findByConfirmToken(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailEqualsOrUserNameEquals(String email,String username);
	
	@Query("select u from User u inner join u.roles r inner join r.permissions where ( u.email = ?1 or u.userName =?1) and u.confirmed = true")
	Optional<User> findByLogin(String login);


	public Optional<User> findByEmailIgnoreCase(String email);

	@EntityGraph(attributePaths = {"roles.permissions"})
	public Optional<User> findByUserNameIgnoreCase(String un);

	//public List<User> findByRoles(Role role);

	public Page<User> findByDisplayNameContainsIgnoreCase(String name,Pageable p);

	//public Page<User> findByDisplayNameContainsIgnoreCaseAndRoles(String name,Role role,Pageable p);

	public Integer countByDisplayNameContainsIgnoreCase(String name);
	
	@Override
	@EntityGraph(attributePaths = {"roles.permissions"})
	public Optional<User> findById(Long id);
	
	/*@Query("select t from User t where ( lower(t.userName) like %?1% or lower(t.email) like %?1% )")
	public Page<User> findByString(String name,Pageable p);
	
	@Query("select count(t) from User t where ( lower(t.userName) like %?1% or lower(t.email) like %?1% )")
	public Integer countByString(String name);*/
	
	public Page<User> findByUserNameContainsOrEmailContains(String s,String s2,Pageable p);
	
	public Integer countByUserNameContainsOrEmailContains(String s,String s2);
	
}
