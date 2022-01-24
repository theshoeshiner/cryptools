package org.thshsh.crypt.tax;

import java.util.HashMap;
import java.util.Map;

public class YearGainSummary {

	Integer year;
	Map<String,AssetGainSummary> accountMap;
	AssetGainSummary total;
	Gains gains;
	
	YearGainSummary(Integer y) {
		this.year = y;
		this.accountMap = new HashMap<>();
		this.total = new AssetGainSummary(null);
		this.gains = new Gains();
	}

	public void aggregate(Sale sale) {
		this.getSummaryForAsset(sale.sellRecord.asset.name).aggregate(sale);
		this.total.aggregate(sale);
	}

	public AssetGainSummary getSummaryForAsset(String asset) {
		if(this.accountMap.get(asset) == null) {
			this.accountMap.put(asset,new AssetGainSummary(asset));
		}
		return this.accountMap.get(asset);
	}

}
