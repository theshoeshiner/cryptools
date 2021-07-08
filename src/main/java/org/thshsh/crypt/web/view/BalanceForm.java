package org.thshsh.crypt.web.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.ExchangeRepository;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.BigDecimalConverter;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class BalanceForm extends EntityForm<Balance, Long> {

	@Autowired
	ApplicationContext context;

	@Autowired
	BalanceRepository balRepo;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	ExchangeRepository exeRepo;

	Portfolio portfolio;
	ComboBox<Exchange> exchangeField;



	public BalanceForm(Balance entity,Portfolio p) {
		super(Balance.class, entity);
		this.portfolio = p;
	}

	@Override
	protected JpaRepository<Balance, Long> getRepository() {
		return balRepo;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setupForm() {

		formLayout.startHorizontalLayout();

		exchangeField = new ComboBox<>("Exchange");
		exchangeField.setItemLabelGenerator(Exchange::getName);
		exchangeField.setItems(context.getBean(HasNameDataProvider.class,exeRepo));
		exchangeField.setWidth("250px");
		formLayout.add(exchangeField);

		formLayout.endLayout();
		formLayout.startHorizontalLayout();

		ComboBox<Currency> ass = new ComboBox<>("Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");
		formLayout.add(ass);

		TextField balance = new TextField("Balance");
		formLayout.add(balance);

		formLayout.endLayout();

		/*binder.forField(exchangeField).asRequired().bind(b -> {
			return null;
			},
			(b,e) -> {

			}
			);*/
		binder.forField(exchangeField).bind(Balance::getExchange,Balance::setExchange);
		binder.forField(ass).asRequired().bind(Balance::getCurrency, Balance::setCurrency);
		binder.forField(balance)
		.asRequired()
		.withNullRepresentation("")
		.withConverter(new BigDecimalConverter())
		.bind(Balance::getBalance, Balance::setBalance);

	}

	@Override
	protected Balance createEntity() {
		LOGGER.info("createEntity: {}",this.portfolio);
		Balance b = super.createEntity();
		if(portfolio!=null) b.setPortfolio(portfolio);
		return b;
	}

	@Override
	protected Long getEntityId(Balance e) {
		return e.getId();
	}

}