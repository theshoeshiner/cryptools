package org.thshsh.crypt.web.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.web.view.EntitiesList.FilterMode;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.cryptman.Account;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.FunctionUtils;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "portfolio", layout = MainLayout.class)
@PageTitle("Portfolio")
public class PortfolioBalancesView extends VerticalLayout {

	@Autowired
	BalanceRepository balRepo;

	@PostConstruct
	public void postConstruct() {

		DelegateEntitiesListProvider<Balance, Long> ep = new DelegateEntitiesListProvider<Balance, Long>() {

			@Override
			public void setupColumns(Grid<Balance> grid) {

			}

			@Override
			public void setFilter(String text) {}

			@Override
			public ExampleFilterRepository<Balance, Long> getRepository() {
				return balRepo;
			}

			@Override
			public String getEntityName(Balance t) {
				return FunctionUtils.nestedValue(Balance::getAccount, Account::getExchange, Exchange::getName).apply(t) +" / "+t.getBalance() +" "+t.getCurrency().getKey();
			}

			@Override
			public Long getEntityId(Balance entity) {
				return entity.getId();
			}

			@Override
			public void delete(Balance t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearFilter() {}
		};

		EntitiesList<Balance, Long> balances = new EntitiesList<>(Balance.class, null, ep,FilterMode.Example);

		this.add(balances);


	}

}

