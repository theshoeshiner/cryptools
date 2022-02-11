package org.thshsh.crypt;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.mutable.MutableObject;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "portfolio")
public class Portfolio extends IdedEntity implements HasName {

	@ManyToOne
	User user;

	@Column
	String name;

	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Set<Balance> balances; 


	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Set<PortfolioHistory> histories;
	
	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Set<PortfolioAlert> alerts;
	
	@ManyToOne(cascade = CascadeType.ALL)
	PortfolioHistory latest;
	
	@ManyToOne(cascade = CascadeType.ALL)
	PortfolioAlert latestAlert;

	@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Set<Allocation> allocations; 

	@Embedded
	PortfolioSettings settings;

	public Portfolio() {}
	
	public Portfolio(String n) {
		this.name=n;
	}

	public Set<Allocation> getAllocations() {
		if(allocations == null) allocations = new HashSet<>();
		return allocations;
	}
	
	public Optional<Allocation> getAllocation(Currency c) {
		return getAllocations().stream().filter(a -> a.getCurrency().equals(c)).findFirst();
	}
	
	public Boolean hasUndefinedAllocation() {
		return getAllocations().stream().filter(a -> a.isUndefined()).findAny().isPresent();
	}

	public void setAllocations(Set<Allocation> allocations) {
		this.allocations = allocations;
	}

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
		if(balances == null) balances = new HashSet<Balance>();
		return balances;
	}
	
	public Set<Balance> getBalances(Currency c) {
		return getBalances().stream().filter(b -> b.getCurrency().equals(c)).collect(Collectors.toSet());
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
	
	


	public Set<PortfolioHistory> getHistories() {
		if(histories==null)histories = new HashSet<>();
		return histories;
	}

	public PortfolioAlert getLatestAlert() {
		return latestAlert;
	}

	public PortfolioHistory getLatest() {
		return latest;
	}

	public void setLatest(PortfolioHistory latest) {
		this.latest = latest;
	}

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
	
	public BigDecimal getAllocationRemainder() {
		return BigDecimal.ONE.subtract(getAllocationTotal());
	}

	public BigDecimal getAllocationTotal() {
		//BigDecimal total = BigDecimal.ZERO;
		//MutableBigDecimal mbd;
		MutableObject<BigDecimal> total = new MutableObject<>(BigDecimal.ZERO);
		getAllocations().forEach(all -> {
			if(all.getPercent()!=null) total.setValue(total.getValue().add(all.getPercent()));
		});
		return total.getValue();
	}

	public void setLatestAlert(PortfolioAlert latestAlert) {
		this.latestAlert = latestAlert;
	}
	
	
}
