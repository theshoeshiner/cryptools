package org.thshsh.cryptman;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.crypt.User;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "portfolio")
public class Portfolio extends IdedEntity {

	@ManyToOne
	User user;

	@Column
	String name;

	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	Set<Balance> balances;


	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Set<PortfolioHistory> histories;

	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	Set<Allocation> allocations;

	@Embedded
	PortfolioSettings settings;

	//@ManyToOne(optional = false)
	//Currency reserve;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Balance> getBalances() {
		return balances;
	}

	public Set<Currency> getCurrencies(){
		Set<Currency> s = new HashSet<Currency>();
		getBalances().forEach(b -> {
			s.add(b.getCurrency());
		});
		return s;
	}

	public void setBalances(Collection<Balance> balances) {
		if(balances instanceof Set) this.balances = (Set<Balance>) balances;
		else this.balances = new HashSet<>(balances);
	}

	/*
	 * public Currency getReserve() { return reserve; }
	 *
	 * public void setReserve(Currency reserve) { this.reserve = reserve; }
	 */

	@Override
	public String toString() {
		return "Portfolio [id=" + id + ", user=" + user + ", name=" + name + "]";
	}

	public PortfolioSettings getSettings() {
		if(settings == null) settings = new PortfolioSettings();
		return settings;
	}

	public void setSettings(PortfolioSettings settings) {
		this.settings = settings;
	}


}
