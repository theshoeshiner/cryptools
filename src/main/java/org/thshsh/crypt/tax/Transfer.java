package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Transfer {

	Long id;
	List<Transaction> transactions;
	BigDecimal quantityFrom;
	BigDecimal quantityTo;
	BigDecimal feeFrom;

	public Transfer(List<Transaction> t){
		//TODO
		this.id = generateId();
		this.transactions = new ArrayList<>(t);


		this.init();
	}

	public void init(){
		BigDecimal wd = BigDecimal.ZERO;
		BigDecimal dp = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		for(int i=0;i<this.transactions.size();i++){
			Transaction t = this.transactions.get(i);
			if(t.quantityFrom != null) wd = wd.add(t.quantityFrom);
			if(t.quantityTo != null) dp = dp.add(t.quantityTo);
			if(t.feeFrom != null) fee = fee.add(t.feeFrom);
		}
		this.quantityFrom = wd;
		this.quantityTo = dp;
		BigDecimal missingFee = wd.subtract(dp);
		this.feeFrom = fee.add(missingFee);
		//mark this as the fee
	}

	long idSequence = 10000;

	long generateId(){
		return idSequence++;
	}
}
