package org.thshsh.crypt.web.view.rate;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.repo.MarketRateRepository;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.portfolio.PortfolioEntryGrid;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.data.QueryByExampleDataProvider;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class MarketRateGrid extends AppEntityGrid<MarketRate> {


	public MarketRateGrid() {
		super( null, FilterMode.Example);
		this.defaultSortOrderProperty="timestamp";
		this.defaultSortAsc=false;
		this.showDeleteButton=false;
		this.showEditButton=false;
		this.appendButtonColumn=true;
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		buttonColumn.setFlexGrow(1);
	}

	
	
	/*@Override
	public DataProvider<MarketRate, ?> createBaseDataProvider() {
		QueryByExampleDataProvider<MarketRate> dataProvider = new QueryByExampleDataProvider<>((MarketRateRepository)getRepository(),MarketRate.class);
		dataProvider.setMatcher(
				ExampleMatcher
				.matchingAny()
				.withStringMatcher(ExampleMatcher.StringMatcher.STARTING)
				.withIgnoreCase()
				.withIgnoreNullValues()
				);
		return dataProvider;
	}*/



	@Override
	public void setupColumns(Grid<MarketRate> grid) {
		
		grid.addColumn(BinderUtils.nestedValue(MarketRate::getCurrency, Currency::getDisplayName))
		.setHeader("Currency")
		.setWidth("250px")
		.setFlexGrow(0)
		.setSortProperty("name","key")
		;
		
		/*grid.addColumn(BinderUtils.nestedValue(MarketRate::getCurrency, Currency::getKey))
		.setHeader("Currency")
		.setWidth("150px")
		.setFlexGrow(0)
		;*/
		
		grid.addColumn(new TemporalRenderer<>(MarketRate::getTimestamp,DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
		.setHeader("Timestamp")
		.setWidth("150px")
		.setFlexGrow(0)
		.setSortProperty("timestamp")
		;
		
		grid.addColumn(new NumberRenderer<>(MarketRate::getRate, PortfolioEntryGrid.ReserveFormatFull))
		.setHeader("Rate")
		.setTextAlign(ColumnTextAlign.END)
		.setWidth("150px")
		.setFlexGrow(0)
		.setSortable(false)
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

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<MarketRate, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<MarketRate, Long> repository) {
		super.setRepository(repository);
	}
	
	

}
