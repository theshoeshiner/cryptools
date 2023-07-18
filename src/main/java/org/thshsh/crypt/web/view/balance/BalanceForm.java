package org.thshsh.crypt.web.view.balance;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.BigDecimalConverter;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.serv.BalanceService;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.serv.MarketRateService;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.crypt.web.view.HasNameDataProvider;
import org.thshsh.crypt.web.view.currency.CurrencyDataProvider;
import org.thshsh.vaadin.SingleCheckboxGroup;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class BalanceForm extends AppEntityForm<Balance, Long> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(BalanceForm.class);

	@Autowired
	ApplicationContext context;

	@Autowired
	BalanceService balService;
	

	@Autowired
	PortfolioHistoryRepository histRepo;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	ExchangeRepository exeRepo;
	
	@Autowired
	MarketRateService rateService;

	@Autowired
	ManagePortfolioService manageService;
	
	Portfolio portfolio;
	ComboBox<Exchange> exchangeField;
	SingleCheckboxGroup detectAllocationField;


	public BalanceForm(Balance entity,Portfolio p) {
		super(entity);
		this.portfolio = p;
		this.createText = "Add";
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

		formLayout.endComponent();
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
		
		formLayout.endComponent();
		
		detectAllocationField = new SingleCheckboxGroup("Detect & Adjust Allocation");
		//detectAllocation.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
		//detectAllocation.setItems("Detect Allocation %");
		detectAllocationField.setHelperText("Automatically detects and sets allocation for this Currency. Beware this may override other allocations.");
		formLayout.add(detectAllocationField);

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
		.withNullRepresentation(StringUtils.EMPTY)
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
	protected void bind() throws ValidationException {
		super.bind();
		
	}

	@Override
	protected Balance persist() {
		Balance b = super.persist();
		if(detectAllocationField.isTrue()) {
			PortfolioHistory h = manageService.createHistory(portfolio, false);
			balService.autoDetectAllocation(entity.getCurrency(), portfolio, h);
		}
		return b;
	}


	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Balance, Long> descriptor) {
		super.setDescriptor(descriptor);
	}


	@Override
	@Autowired
	public void setRepository(CrudRepository<Balance, Long> repository) {
		super.setRepository(repository);
	}
	
	


}
