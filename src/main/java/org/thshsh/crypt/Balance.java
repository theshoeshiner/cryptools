package org.thshsh.crypt;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "balance",indexes = {
		@Index(columnList = "portfolio_id")
})
public class Balance extends IdedEntity {

	@ManyToOne
	Account account;

	@ManyToOne
	Exchange exchange;

	@ManyToOne(optional = false)
	Currency currency;

	@Column(columnDefinition = "decimal")
	//@Convert(converter = BigDecimalToStringConverter.class)
	BigDecimal balance;

	@ManyToOne
	Portfolio portfolio;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}


	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	@Override
	public String toString() {
		return "[exchange=" + exchange + ", currency=" + currency + ", balance=" + balance + ", portfolio="
				+ portfolio + "]";
	}



}
