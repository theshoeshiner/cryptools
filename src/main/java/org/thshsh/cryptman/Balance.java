package org.thshsh.cryptman;

import java.math.BigDecimal;

import javax.persistence.BigDecimalToStringConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thshsh.crypt.Currency;
import org.thshsh.crypt.IdedEntity;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "balance")
public class Balance extends IdedEntity {

	@ManyToOne
	Account account;

	@ManyToOne(optional = false)
	Currency currency;

	@Column
	@Convert(converter = BigDecimalToStringConverter.class)
	BigDecimal balance;

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


}
