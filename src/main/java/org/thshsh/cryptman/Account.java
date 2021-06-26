package org.thshsh.cryptman;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.crypt.CryptModel;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.crypt.User;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "account")
public class Account extends IdedEntity {

	@ManyToOne
	Exchange exchange;

	@ManyToOne
	Portfolio portfolio;


	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	/*public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	*/



}
