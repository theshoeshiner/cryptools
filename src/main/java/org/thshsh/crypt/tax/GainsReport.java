package org.thshsh.crypt.tax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GainsReport {

	public static final Logger LOGGER = LoggerFactory.getLogger(GainsReport.class);

	Map<Integer,List<GainRecord>> yearMap;

	GainsReport() {

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
		Integer[] years = new Integer[] {2017,2018,2019,2020,2021};

		for(Integer year : years){
			RecordGains g = this.processYear(year);
			LOGGER.warn("{} Gains - long: {} short: {} income: {}",new Object[] {year,g.longTermGain,g.shortTermGain,g.incomeGain});
		}
		/*
		this.LOGGER.warn("2017 Gains - long: {} short: {} income: {}",[g17.longTermGain,g17.shortTermGain,g17.incomeGain]);
		this.LOGGER.warn("2018 Gains - long: {} short: {}",[g18.longTermGain,g18.shortTermGain]);
		this.LOGGER.warn("2019 Gains - long: {} short: {}",[g19.longTermGain,g19.shortTermGain]);
		this.LOGGER.warn("2020 Gains - long: {} short: {}",[g20.longTermGain,g20.shortTermGain]);
		this.LOGGER.warn("2021 Gains - long: {} short: {}",[g21.longTermGain,g21.shortTermGain]);
		*/
	}

	public RecordGains processYear(Integer year){

		RecordGains gains = new RecordGains();

		YearSummary summary = new YearSummary();

		for(GainRecord record : this.yearMap.get(year)){
			if(record.gains != null) {

				//this.LOGGER.info("adding record: {}",[record]);
				//this.LOGGER.info("adding - long: {} short: {} income: {}",[record.gains.longTermGain,record.gains.shortTermGain,record.gains.incomeGain]);
				gains.addGains(record.gains);
				//this.LOGGER.info("total - long: {} short: {}",[gains.longTermGain,gains.shortTermGain]);

				if(record instanceof SellRecord) {
					SellRecord sell = (SellRecord) record;
					if(sell.saleRecords != null) {
						for(Sale sr : sell.saleRecords){
							summary.aggregate(sr);
						}
					}
				}
			}
		}


		LOGGER.info("Yearly Summary: {}",summary);

		return gains;
	}

	List<GainRecord> getRecordsForYear(Integer year){
		if(this.yearMap.get(year) == null) this.yearMap.put(year,new ArrayList<>());
		return this.yearMap.get(year);
	}


}
