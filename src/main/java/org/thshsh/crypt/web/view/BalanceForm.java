package org.thshsh.crypt.web.view;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.BigDecimalConverter;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class BalanceForm extends AppEntityForm<Balance, Long> {

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
		ass.setItems(context.getBean(CurrencyDataProvider.class));
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
		binder.forField(exchangeField).asRequired().bind(Balance::getExchange,Balance::setExchange);
		binder.forField(ass).asRequired().bind(Balance::getCurrency, Balance::setCurrency);
		binder.forField(balance)
		.asRequired()
				/*.withValidator((s,c) -> {
					try {
						new BigDecimal(s);
						return ValidationResult.ok();
					}
					catch (NumberFormatException e) {
						return ValidationResult.error("Invalid Format");
					}
				})*/
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
