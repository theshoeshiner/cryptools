package org.thshsh.crypt.tax;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GainsReport {

	public static final Logger LOGGER = LoggerFactory.getLogger(GainsReport.class);

	Map<Integer,List<GainRecord>> yearMap;

	Integer[] years;
	
	GainsReport(Integer[] y) {
		this.years = y;
		this.yearMap = new HashMap<>();

	}

	public void addRecords(List<Record> records) {

		for(Record record : records){
			if(record instanceof GainRecord) {
				GainRecord gr = (GainRecord)record;
				Integer year = record.transaction.timestamp.getYear();
				this.getRecordsForYear(year).add(gr);
			}
		}

		/*
		this.LOGGER.info("2017 records: {}",this.yearMap.get(2017).length);
		this.LOGGER.info("2018 records: {}",this.yearMap.get(2018).length);
		this.LOGGER.info("2019 records: {}",this.yearMap.get(2019).length);
		this.LOGGER.info("2020 records: {}",this.yearMap.get(2020).length);


		var g17 = this.processYear(2017);
		var g18 = this.processYear(2018);
		var g19 = this.processYear(2019);
		var g20 = this.processYear(2020);
		var g21 = this.processYear(2021);
		*/
		//

		List<YearGainSummary> summaries = new ArrayList<YearGainSummary>();
		
		for(Integer year : years){
			summaries.add(this.processYear(year));
			
		}
		
		
		summaries.forEach(sum -> {
			
			//LOGGER.warn("{} Gains - long: {} short: {} income: {}",new Object[] {sum.year,sum.gains.longTermGain,sum.gains.shortTermGain,sum.gains.incomeGain});
			LOGGER.warn("{} Gains",sum.year);
			LOGGER.warn("Long:  basis: {} proceeds: {} gain: {}",
					sum.gains.longTermBasis.setScale(2, RoundingMode.HALF_EVEN),
					sum.gains.longTermProceeds.setScale(2, RoundingMode.HALF_EVEN),
					sum.gains.longTermGain.setScale(2, RoundingMode.HALF_EVEN));
			LOGGER.warn("Short: basis: {} proceeds: {} gain: {}",
					sum.gains.shortTermBasis.setScale(2, RoundingMode.HALF_EVEN),
					sum.gains.shortTermProceeds.setScale(2, RoundingMode.HALF_EVEN),
					sum.gains.shortTermGain.setScale(2, RoundingMode.HALF_EVEN));
			LOGGER.warn("Income: {}",sum.gains.incomeGain.setScale(2, RoundingMode.HALF_EVEN));
			
		});
		
		/*
		this.LOGGER.warn("2017 Gains - long: {} short: {} income: {}",[g17.longTermGain,g17.shortTermGain,g17.incomeGain]);
		this.LOGGER.warn("2018 Gains - long: {} short: {}",[g18.longTermGain,g18.shortTermGain]);
		this.LOGGER.warn("2019 Gains - long: {} short: {}",[g19.longTermGain,g19.shortTermGain]);
		this.LOGGER.warn("2020 Gains - long: {} short: {}",[g20.longTermGain,g20.shortTermGain]);
		this.LOGGER.warn("2021 Gains - long: {} short: {}",[g21.longTermGain,g21.shortTermGain]);
		*/
	}

	public YearGainSummary processYear(Integer year){

		LOGGER.info("processYear: {}",year);
		
		List<Sale> sales = new ArrayList<Sale>();
		
		YearGainSummary summary = new YearGainSummary(year);

		for(GainRecord record : this.yearMap.get(year)){
			if(record.gains != null) {

				LOGGER.info("gains: {} record: {}",record.gains, record);
				//this.LOGGER.info("adding - long: {} short: {} income: {}",[record.gains.longTermGain,record.gains.shortTermGain,record.gains.incomeGain]);
				summary.gains.addGains(record.gains);
				//this.LOGGER.info("total - long: {} short: {}",[gains.longTermGain,gains.shortTermGain]);

				if(record instanceof SellRecord) {
					SellRecord sell = (SellRecord) record;
					if(sell.saleRecords != null) {
						for(Sale sr : sell.saleRecords){
							summary.aggregate(sr);
							sales.add(sr);
						}
					}
				}
			}
		}

		Collections.sort(sales, (s0,s1) -> {
			return s1.sortRecord.taxPer.multiply(s1.quantity).compareTo(s0.sortRecord.taxPer.multiply(s0.quantity));
		});

		LOGGER.info("worst sales:");
		
		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
		
		for(Sale sale : sales) {
			LOGGER.info("{} Tax: {} on {} {}",
					dtf.format(sale.sellRecord.timestamp),
					sale.sortRecord.taxPer.multiply(sale.quantity).setScale(2, RoundingMode.HALF_EVEN),
					sale.quantity,
					sale.sellRecord.asset
					);
		}
		
		LOGGER.info("Yearly Summary: {}",summary);

		return summary;
	}

	List<GainRecord> getRecordsForYear(Integer year){
		if(this.yearMap.get(year) == null) this.yearMap.put(year,new ArrayList<>());
		return this.yearMap.get(year);
	}


}
