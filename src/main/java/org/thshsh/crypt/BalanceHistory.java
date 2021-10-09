package org.thshsh.crypt;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity()
@Table(schema = CryptmanModel.SCHEMA, name = "balance_history")
public class BalanceHistory extends IdedEntity {

	@ManyToOne(optional = false)
	Currency currency;

	@Column(columnDefinition = "decimal")
	BigDecimal balance;

	@Column(columnDefinition = "decimal")
	BigDecimal value;

	@ManyToOne
	PortfolioHistory portfolio;

	public BalanceHistory() {}

	public BalanceHistory(PortfolioHistory state,Balance b, BigDecimal value) {
		this.currency = b.getCurrency();
		this.balance = b.getBalance();
		this.portfolio = state;
		this.value = value;
	}






}
