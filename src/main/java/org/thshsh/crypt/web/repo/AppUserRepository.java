package org.thshsh.crypt.web.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thshsh.crypt.User;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface AppUserRepository extends BaseRepository<User, Long>, ExampleFilterRepository<User, Long> {

	public Optional<User> findByEmailIgnoreCase(String email);

	//public List<User> findByRoles(Role role);

	public Page<User> findByDisplayNameContainsIgnoreCase(String name,Pageable p);

	//public Page<User> findByDisplayNameContainsIgnoreCaseAndRoles(String name,Role role,Pageable p);

	public Integer countByDisplayNameContainsIgnoreCase(String name);

	//public Integer countByDisplayNameContainsIgnoreCaseAndRoles(String name,Role role);

	//@Query("select distinct u from User u join u.roles rls join rls.permissions perms where perms.feature =?1 and perms.access =?2")
	//public List<User> findByFeatureAccess(Feature feature, FeatureAccess access);

	//@Query("select distinct u from User u join u.roles rls join rls.permissions perms where lower(u.displayName) like lower(concat('%', ?1,'%')) and perms.feature =?2 and perms.access =?3")
	//public Page<User> findByDisplayNameAndFeatureAccess(String name,Feature feature, Access access,Pageable p);

	//@Query("select count(distinct u.id) from User u join u.roles rls join rls.permissions perms where lower(u.displayName) like lower(concat('%', ?1,'%')) and perms.feature =?2 and perms.access =?3")
	//public Integer countByDisplayNameAndFeatureAccess(String name,Feature feature, Access access);
}
