package org.thshsh.crypt.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.User;
import org.thshsh.vaadin.data.SecuredStringSearchRepository;

public interface PortfolioRepository extends BaseRepository<Portfolio, Long>, 
HasNameRepository<Portfolio> ,
SecuredStringSearchRepository<Portfolio, Long>
{

	public static final String OWNER_OR_SUPER_IN = "e.id in ( select e.id from Portfolio e join e.user owners where ( owners = ?#{session.user} or ?#{session.hasAccess(T(org.thshsh.crypt.Feature).Portfolio,T(org.thshsh.crypt.Access).Super)} = true ) )";
	
	public Page<Portfolio> findByUser(User user,Pageable p);

	public Long countByUser(User user);
	
	public List<Portfolio> findAllByUser(User user);
	
	@Query("select e from #{#entityName} e where e.id =?1 and "+OWNER_OR_SUPER_IN)
	public Portfolio findByIdSecured(Long is);

	@Query("select e from #{#entityName} e join e.user owner where "+OWNER_OR_SUPER_IN)
	public Page<Portfolio> findAllSecured(Pageable p);
	
	@Query("select count(distinct e.id) from #{#entityName} e where "+OWNER_OR_SUPER_IN)
	public  Long countSecured();
	
	@Query("select distinct e from #{#entityName} e where "
			+ "( lower(e.user.email) like %?1% or lower(e.user.userName) like %?1% or lower(e.name) like %?1% )"
			+ " and "+OWNER_OR_SUPER_IN)
	Page<Portfolio> findByStringSecured(String s, Pageable p);
	
	@Query("select count(distinct e.id) from #{#entityName} e where "
			+ "( lower(e.name) like %?1%  ) and "+OWNER_OR_SUPER_IN)
	Long countByStringSecured(String s);
	
	@Query("delete from Portfolio p where p.id = ?1")
	@Modifying
	public void deleteById(Long id);
	

	@Query("select e.currency.key from #{#entityName} p join p.latest.entries e where p.id = ?1 order by e.value desc NULLS LAST")
	public List<String> findCurrencySymbols(Long id);
	
	@Query("select distinct b.exchange.name from #{#entityName} p join p.balances b where p.id = ?1")
	public List<String> findExchangeNames(Long id);
	
	//@Query("select a.currency.key from #{#entityName} a where a.portfolio = ?1 order by a.percent desc NULLS LAST")
	//public List<String> findExchangeNames(Portfolio p);
}

