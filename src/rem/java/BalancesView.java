package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.cryptman.Account;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.FunctionUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "balances", layout = MainLayout.class)
@PageTitle("Balances")
public class BalancesView extends EntitiesView<Balance, Long> {

	@Autowired
	BalanceRepository balRepo;

	public BalancesView() {
		super(Balance.class, BalanceDialog.class);
	}

	@Override
	public ExampleFilterRepository<Balance, Long> getRepository() {
		return balRepo;
	}

	@PostConstruct
	public void postConstruct() {
		entitiesList.showButtonColumn = true;
		super.postConstruct();
	}

	@Override
	public String getEntityName(Balance t) {
		return FunctionUtils.nestedValue(Balance::getAccount, Account::getExchange, Exchange::getName).apply(t) +" / "+t.getBalance() +" "+t.getCurrency().getKey();
	}

	@Override
	public void setupColumns(Grid<Balance> grid) {


		grid.addColumn(FunctionUtils.nestedValue(Balance::getCurrency, Currency::getDisplayName))
		.setHeader("Currency")
		.setSortProperty("currency.name")
		;

		grid.addColumn(FunctionUtils.nestedValue(Balance::getAccount, Account::getExchange, Exchange::getName))
		.setHeader("Exchange")
		.setSortProperty("account.exchange.name")
		;

		grid.addColumn(Balance::getBalance)
		.setHeader("Balance")
		.setSortProperty("balance")
		;

	}



	@Override
	public void setFilter(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearFilter() {
		// TODO Auto-generated method stub

	}



}
