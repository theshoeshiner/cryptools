package org.thshsh.crypt.web.view;

import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.cryptman.Account;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.GridField;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;

public class PortfolioView extends EntityView<Portfolio> {

	public PortfolioView() {
		super(Portfolio.class, null);
	}

	@Override
	protected void setupForm() {

		TextField name = new TextField("Name");


		Grid<Balance> grid = new Grid<>();

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

		GridField<Balance> balanceField = new GridField<>(grid);
		grid.setDataProvider(balanceField.getDataProvider());

		binder.forField(name).bind(Portfolio::getName, Portfolio::setName);

		binder.forField(balanceField).bind(Portfolio::getBalances, Portfolio::setBalances);

		formLayout.startVerticalLayout();
		formLayout.add(name);
		formLayout.add(grid);
		formLayout.endLayout();

	}

	@Override
	protected void setupBreadcumbs() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getEntityLabel() {
		return entity.getName();
	}

}
