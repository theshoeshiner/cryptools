package org.thshsh.crypt;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.mutable.MutableObject;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(schema = CryptmanModel.SCHEMA, name = "portfolio_history",indexes = {
		@Index(columnList = "portfolio_id")
})
public class PortfolioHistory extends IdedEntity {

    @ManyToOne
    Portfolio portfolio;

    @Column
    ZonedDateTime timestamp;

    //@OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
    //Set<BalanceHistory> balances;

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    Set<PortfolioEntryHistory> entries;

    @Column(columnDefinition = "decimal")
    BigDecimal value;

    @Column(columnDefinition = "decimal")
    BigDecimal maxToTriggerPercent;

    
    @Column(columnDefinition = "decimal")
    BigDecimal totalAdjustPercent;
    
    

    public PortfolioHistory() {}
    
    

    public PortfolioHistory(Portfolio portfolio) {
		super();
		this.portfolio = portfolio;
		this.timestamp = ZonedDateTime.now();
	}



	public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }



    public Set<PortfolioEntryHistory> getEntries() {
        if(entries == null) entries = new HashSet<PortfolioEntryHistory>();
        return entries;
    }


    public void setEntries(Set<PortfolioEntryHistory> entries) {
        this.entries = entries;
    }
    
    public Optional<PortfolioEntryHistory> getEntry(Currency c) {
    	return getEntries().stream().filter(e -> Objects.equals(e.getCurrency(), c)).findFirst();
    }
    
    public Optional<BigDecimal> getBalance(Currency c) {
    	return getEntries().stream().filter(e -> Objects.equals(e.getCurrency(), c)).findFirst().map(e -> e.getBalance());
    }
    
    
    
    public Optional<BigDecimal> getDefinedAllocation(Currency c) {
    	
    	return getEntries()
    			.stream()
    			.filter(e -> Objects.equals(e.getCurrency(), c))
    			.findFirst()
    			.filter(e -> !e.getAllocationUndefined())
    			.map(e -> e.getAllocationPercent());
    }

    public PortfolioEntryHistory getMaxTriggerEntry() {
        MutableObject<PortfolioEntryHistory> max = new MutableObject<PortfolioEntryHistory>();
        getEntries().forEach(entry -> {
            if(max.getValue() == null || max.getValue().getToTriggerPercent().compareTo(entry.getToTriggerPercent()) < 0) {
                max.setValue(entry);
            }
        });
        return max.getValue();
    }
    
    public BigDecimal getAllocated() {
    	MutableObject<BigDecimal> allocated = new MutableObject<BigDecimal>(BigDecimal.ZERO);
    	getEntries().forEach(e -> {
    		if(!e.getAllocationUndefined())
    			allocated.setValue(allocated.getValue().add(e.getAllocationPercent()));
    	});
    	return allocated.getValue();
    }

    public Stream<PortfolioEntryHistory> getTopEntries(long count) {
    	return getEntries().stream().sorted((e0,e1) -> {
    		return e1.getValue().compareTo(e0.getValue());
    	}).limit(count);
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMaxToTriggerPercent() {
        return maxToTriggerPercent;
    }

    public void setMaxToTriggerPercent(BigDecimal maxToTriggerPercent) {
        this.maxToTriggerPercent = maxToTriggerPercent;
    }

    

    public BigDecimal getTotalAdjustPercent() {
		return totalAdjustPercent;
	}

	public void setTotalAdjustPercent(BigDecimal totalAdjustPercent) {
		this.totalAdjustPercent = totalAdjustPercent;
	}


    @Override
    public String toString() {
        return "[id=" + id + ", timestamp=" + timestamp + ", value=" + value + ", maxToTriggerPercent="
                + maxToTriggerPercent + "]";
    }




}
