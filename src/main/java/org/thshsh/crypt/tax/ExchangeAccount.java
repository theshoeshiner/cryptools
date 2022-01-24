package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents an account at a specific exchange
 */
public class ExchangeAccount {


	public static final Logger LOGGER = LoggerFactory.getLogger(ExchangeAccount.class);

	String id;
	Map<String,BigDecimal> balances;

	public ExchangeAccount(String id) {
		this.id = id;
		this.balances = new HashMap<>();
	}

	public BigDecimal getBalance(String name) {
		if (!this.balances.containsKey(name)) this.balances.put(name, BigDecimal.ZERO);
		return this.balances.get(name);
	}

	public void setBalance(String name, BigDecimal balance){
		if(balance.compareTo(BigDecimal.ZERO) == 0) this.balances.remove(name);
		else this.balances.put(name, balance);
	}

}
