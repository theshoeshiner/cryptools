package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.ExchangeRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.cryptman.Account;
import org.thshsh.cryptman.AccountRepository;
import org.thshsh.cryptman.Allocation;
import org.thshsh.cryptman.AllocationRepository;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.BigDecimalConverter;
import org.thshsh.cryptman.Portfolio;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;



@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class AllocationDialog extends EntityDialog<Allocation> {

	public static final Logger LOGGER = LoggerFactory.getLogger(AllocationDialog.class);

	@Autowired
	ApplicationContext context;

	//@Autowired
	//AssetNameDataProvider assetNamePro;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	ExchangeRepository exeRepo;

	/*@Autowired
	BalanceRepository balRepo;*/

	@Autowired
	AllocationRepository alloRepo;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	AppSession session;

	Portfolio portfolio;

	@Autowired
	@Qualifier("hundred")
	BigDecimal hundred;

	public AllocationDialog(Allocation en,Portfolio p) {
		super(en, Allocation.class);
		this.portfolio = p;
	}



	@Override
	protected Allocation createEntity() {
		LOGGER.info("createEntity: {}",this.portfolio);
		Allocation b = super.createEntity();
		if(portfolio!=null) b.setPortfolio(portfolio);
		return b;
	}



	/*public BalanceDialog(Balance en) {
		super(en, Balance.class);
	}*/

	@PostConstruct
	public void postConstruct() {
		super.postConstruct(alloRepo);
	}

	ComboBox<Exchange> exchangeField;

	@SuppressWarnings("unchecked")
	@Override
	protected void setupForm() {

		formLayout.startHorizontalLayout();

		/*exchangeField = new ComboBox<>("Exchange");
		exchangeField.setItemLabelGenerator(Exchange::getName);
		exchangeField.setItems(context.getBean(HasNameDataProvider.class,exeRepo));
		exchangeField.setWidth("250px");
		formLayout.add(exchangeField);*/

		BigDecimalField percent = new BigDecimalField("Percent");

		formLayout.add(percent);

		formLayout.endLayout();
		formLayout.startHorizontalLayout();

		ComboBox<Currency> ass = new ComboBox<>("Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");
		formLayout.add(ass);

		/*TextField balance = new TextField("Balance");
		formLayout.add(balance);*/

		formLayout.endLayout();

		/*binder.forField(exchangeField).asRequired().bind(b -> {
			return null;
			},
			(b,e) -> {

			}
			);*/
		//binder.forField(exchangeField).bind(Balance::getExchange,Balance::setExchange);
		binder.forField(ass)
		.asRequired()
		.bind(Allocation::getCurrency, Allocation::setCurrency);

		binder.forField(percent)
		.asRequired()
		.withConverter(v -> {
			if(v == null) return null;
			else return v.divide(hundred);
		}, v -> {
			if(v == null) return null;
			else return v.multiply(hundred);
		})
		//.withNullRepresentation("")
		//.withConverter(new BigDecimalConverter())
		.bind(Allocation::getPercent, Allocation::setPercent);



	}



	@Override
	protected void bind() throws ValidationException {
		super.bind();
		if(this.create) {
			alloRepo.findByPortfolioAndCurrency(portfolio, this.entity.getCurrency()).ifPresent(a -> {
				this.entity.setId(a.getId());
			});
		}
	}

}
