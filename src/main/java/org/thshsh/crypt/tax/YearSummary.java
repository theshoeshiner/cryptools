package org.thshsh.crypt.tax;

import java.util.HashMap;
import java.util.Map;

public class YearSummary {

	Map<String,AccountSummary> accountMap;
	AccountSummary total;

	YearSummary() {
		this.accountMap = new HashMap<>();
		this.total = new AccountSummary(null);
	}

	public void aggregate(Sale sale) {
		this.getAccount(sale.sellRecord.asset.name).aggregate(sale);
		this.total.aggregate(sale);
	}

	public AccountSummary getAccount(String asset) {
		if(this.accountMap.get(asset) == null) {
			this.accountMap.put(asset,new AccountSummary(asset));
		}
		return this.accountMap.get(asset);
	}

}
