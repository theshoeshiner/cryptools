package org.thshsh.crypt.tax;

public class AccountSummary {

	String asset;
	SaleSummary longTerm;
	SaleSummary shortTerm;


	AccountSummary(String asset){
		this.asset = asset;
		this.longTerm = new SaleSummary();
		this.shortTerm = new SaleSummary();
	}

	void aggregate(Sale sale) {
		if(sale.shortTerm) this.shortTerm.aggregate(sale);
		else this.longTerm.aggregate(sale);
	}


}
