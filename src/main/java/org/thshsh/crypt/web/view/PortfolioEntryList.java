package org.thshsh.crypt.web.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.FunctionUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

public class PortfolioEntryList extends EntitiesList<PortfolioEntry, Long> {

	@Autowired
	ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;

	List<PortfolioEntry> entries;

	public PortfolioEntryList(List<PortfolioEntry> entries) {
		super(PortfolioEntry.class, null);
		this.listOperationProvider = new PortfolioEntriesListProvider();
		this.showHeader=false;
		this.entries = entries;
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct(appContext);
	}

	public PortfolioEntryList(Class<PortfolioEntry> c, Class<? extends Component> ev) {
		super(PortfolioEntry.class, null);
	}

	public class PortfolioEntriesListProvider extends DelegateEntitiesListProvider<PortfolioEntry,Long> {

		public PortfolioEntriesListProvider() {
			this.list = PortfolioEntryList.this;
		}



		@Override
		public DataProvider<PortfolioEntry, ?> createDataProvider() {
			return new ListDataProvider(entries);
		}



		@Override
		public Long getEntityId(PortfolioEntry entity) {
			return 0l;
		}

		@Override
		public void setupColumns(Grid<PortfolioEntry> grid) {
			/*grid.addColumn(FunctionUtils.nestedValue(Balance::getExchange, Exchange::getName))
			.setHeader("Exchange");

			grid.addColumn(FunctionUtils.nestedValue(Balance::getCurrency, Currency::getSymbol))
			.setHeader("Currency");

			grid.addColumn(Balance::getBalance)
			.setHeader("Balance");*/

		}

		@Override
		public void setFilter(String text) {

		}

		@Override
		public void clearFilter() {

		}

		@Override
		public ExampleFilterRepository<PortfolioEntry, Long> getRepository() {
			return null;
		}

		@Override
		public String getEntityName(PortfolioEntry t) {
			return null;
		}

		@Override
		public void delete(PortfolioEntry t) {

		}

	}


}
