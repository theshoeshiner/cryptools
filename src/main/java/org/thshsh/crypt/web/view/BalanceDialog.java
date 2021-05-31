package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.ExchangeRepository;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.BigDecimalConverter;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class BalanceDialog extends EntityDialog<Balance> {

	@Autowired
	ApplicationContext context;

	//@Autowired
	//AssetNameDataProvider assetNamePro;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	ExchangeRepository exeRepo;

	@Autowired
	BalanceRepository balRepo;

	public BalanceDialog(Balance en) {
		super(en, Balance.class);
	}

	@PostConstruct
	public void PostConstruct() {
		super.postConstruct(balRepo);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setupForm() {

		formLayout.startHorizontalLayout();

		ComboBox<Exchange> exe = new ComboBox<>("Exchange");
		exe.setItemLabelGenerator(Exchange::getName);
		exe.setItems(context.getBean(HasNameDataProvider.class,exeRepo));
		formLayout.add(exe);

		ComboBox<Currency> ass = new ComboBox<>("Currency");
		ass.setItemLabelGenerator(Currency::getSymbol);
		ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
		formLayout.add(ass);

		TextField balance = new TextField("Balance");
		formLayout.add(balance);

		formLayout.endLayout();

		binder.forField(ass).bind(Balance::getCurrency, Balance::setCurrency);
		binder.forField(balance)
		.withNullRepresentation("")
		.withConverter(new BigDecimalConverter())
		.bind(Balance::getBalance, Balance::setBalance);

	}

}
