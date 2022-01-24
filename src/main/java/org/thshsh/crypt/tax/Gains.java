package org.thshsh.crypt.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the gains made for a specific GainRecord
 * Can also be used to aggregate gains from multiple records
 * @author Dan
 *
 */
public class Gains {

	List<Sale> shortTermRecords;
	List<Sale> longTermRecords;
	List<Transaction> incomeRecords;

	BigDecimal shortTermGain;
	BigDecimal longTermGain;
	BigDecimal incomeGain;

	BigDecimal shortTermBasis;
	BigDecimal longTermBasis;

	BigDecimal shortTermProceeds;
	BigDecimal longTermProceeds;

	//Transaction transaction;


	public Gains(){
		this.shortTermRecords = new ArrayList<>();
		this.longTermRecords = new ArrayList<>();
		this.incomeRecords = new ArrayList<>();

		this.shortTermGain = BigDecimal.ZERO;
		this.longTermGain = BigDecimal.ZERO;
		this.incomeGain = BigDecimal.ZERO;

		this.shortTermBasis = BigDecimal.ZERO;
		this.longTermBasis = BigDecimal.ZERO;

		this.shortTermProceeds = BigDecimal.ZERO;
		this.longTermProceeds = BigDecimal.ZERO;

		//this.LOGGER = LoggerFactory.getLogger("RecordGains");
	}

	public void addShortTerm(Sale sale) {
		this.shortTermRecords.add(sale);
		this.shortTermGain = this.shortTermGain.add(sale.gain);
		this.shortTermProceeds = this.shortTermProceeds.add(sale.proceeds);
		this.shortTermBasis = this.shortTermBasis.add(sale.basis);
	}

	public void addLongTerm(Sale sale) {
		this.longTermRecords.add(sale);
		this.longTermGain = this.longTermGain.add(sale.gain);
		this.longTermProceeds = this.longTermProceeds.add(sale.proceeds);
		this.longTermBasis = this.longTermBasis.add(sale.basis);
	}

	public void addIncome(Transaction transaction){
		this.incomeRecords.add(transaction);
		this.incomeGain = this.incomeGain.add(transaction.fiatTo);
	}

	public Boolean hasLosses(){
		return this.longTermGain.compareTo(BigDecimal.ZERO) < 0 || this.shortTermGain.compareTo(BigDecimal.ZERO) < 0;
	}

	public void addGains(Gains gains){


		this.shortTermRecords.addAll(gains.shortTermRecords);
		this.longTermRecords.addAll(gains.longTermRecords);

		this.shortTermGain = this.shortTermGain.add(gains.shortTermGain);
		this.longTermGain = this.longTermGain.add(gains.longTermGain);

		this.shortTermProceeds = this.shortTermProceeds.add(gains.shortTermProceeds);
		this.longTermProceeds = this.longTermProceeds.add(gains.longTermProceeds);

		this.shortTermBasis = this.shortTermBasis.add(gains.shortTermBasis);
		this.longTermBasis = this.longTermBasis.add(gains.longTermBasis);

		this.incomeGain = this.incomeGain.add(gains.incomeGain);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[shortTermGain=");
		builder.append(shortTermGain);
		builder.append(", longTermGain=");
		builder.append(longTermGain);
		builder.append(", incomeGain=");
		builder.append(incomeGain);
		builder.append(", shortTermBasis=");
		builder.append(shortTermBasis);
		builder.append(", longTermBasis=");
		builder.append(longTermBasis);
		builder.append(", shortTermProceeds=");
		builder.append(shortTermProceeds);
		builder.append(", longTermProceeds=");
		builder.append(longTermProceeds);
		//uilder.append(", transaction=");
		//builder.append(transaction);
		builder.append("]");
		return builder.toString();
	}
	
	

}
