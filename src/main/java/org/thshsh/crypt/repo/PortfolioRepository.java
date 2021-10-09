package org.thshsh.crypt.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.User;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.ExampleFilterRepository;

public interface PortfolioRepository extends BaseRepository<Portfolio, Long>, ExampleFilterRepository<Portfolio, Long>  {

	public static final String OWNER_OR_SUPER_IN = "e.id in ( select e.id from Portfolio e join e.user owners where ( owners = ?#{session.user} or ?#{session.hasAccess(T(org.thshsh.crypt.Feature).Portfolio,T(org.thshsh.crypt.Access).Super)} = true ) )";
	
	public Page<Portfolio> findByUser(User user,Pageable p);

	public Long countByUser(User user);
	
	public List<Portfolio> findAllByUser(User user);

	@Query("select distinct e from #{#entityName} e  where "+OWNER_OR_SUPER_IN)
	//@EntityGraph(attributePaths = {})
	public Page<Portfolio> findAllSecured(Pageable p);
	
	@Query("select count(distinct e.id) from #{#entityName} e where "+OWNER_OR_SUPER_IN)
	public  Long countAllSecured();
	
	@Query("select distinct e from #{#entityName} e where "
			+ "( lower(e.name) like %?1%  ) and "+OWNER_OR_SUPER_IN)
	Page<Portfolio> findByStringSecured(String s, Pageable p);
	
	@Query("select count(distinct e.id) from #{#entityName} e where "
			+ "( lower(e.name) like %?1%  ) and "+OWNER_OR_SUPER_IN)
	Long countByStringSecured(String s);
	
	@Query("delete from Portfolio p where p.id = ?1")
	@Modifying
	public void deleteById(Long id);
}

