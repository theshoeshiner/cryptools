package org.thshsh.crypt.tax;

/**
 * Represents an aggregate set of gains for a specific asset
 * @author Dan
 *
 */
public class AssetGainSummary {

	String asset;
	GainSummary longTerm;
	GainSummary shortTerm;


	AssetGainSummary(String asset){
		this.asset = asset;
		this.longTerm = new GainSummary();
		this.shortTerm = new GainSummary();
	}

	void aggregate(Sale sale) {
		if(sale.shortTerm) this.shortTerm.aggregate(sale);
		else this.longTerm.aggregate(sale);
	}


}
