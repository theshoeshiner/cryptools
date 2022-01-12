package org.thshsh.crypt.web.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.repo.MarketRateRepository;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.ZonedDateTimeRenderer;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class MarketRateGrid extends AppEntityGrid<MarketRate> {

	@Autowired
	MarketRateRepository rateRepo;
	
	public MarketRateGrid() {
		super(MarketRate.class, null, FilterMode.Example);
		this.defaultSortOrderProperty="timestamp";
		this.defaultSortAsc=false;
		this.showButtonColumn=true;
		this.showDeleteButton=false;
		this.showEditButton=false;
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		buttonColumn.setFlexGrow(1);
	}

	
	
	@Override
	public DataProvider<MarketRate, ?> createDataProvider() {
		ExampleFilterDataProvider<MarketRate, Long> dataProvider = new ExampleFilterDataProvider<>(rateRepo,
				ExampleMatcher.matchingAny().withStringMatcher(ExampleMatcher.StringMatcher.STARTING).withIgnoreCase().withIgnoreNullValues(),
				getDefaultSortOrder());
		
		return dataProvider;
	}



	@Override
	public PagingAndSortingRepository<MarketRate, Long> getRepository() {
		return rateRepo;
	}

	@Override
	public void setupColumns(Grid<MarketRate> grid) {
		
		grid.addColumn(FunctionUtils.nestedValue(MarketRate::getCurrency, Currency::getName))
		.setHeader("Currency")
		.setWidth("250px")
		.setFlexGrow(0)
		;
		
		grid.addColumn(FunctionUtils.nestedValue(MarketRate::getCurrency, Currency::getKey))
		.setHeader("Currency")
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
		grid.addColumn(new ZonedDateTimeRenderer<>(MarketRate::getTimestamp,DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Timestamp")
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
		grid.addColumn(new NumberRenderer<>(MarketRate::getRate, PortfolioEntryGrid.ReserveFormatFull))
		.setHeader("Rate")
		.setTextAlign(ColumnTextAlign.END)
		.setWidth("150px")
		.setFlexGrow(0)
		;
		
	}

	@Override
	public void setFilter(String text) {
		filterEntity.setCurrency(new Currency(text,text,text));
	}

	@Override
	public void clearFilter() {
		filterEntity.setCurrency(null);
	}
	
	

}
