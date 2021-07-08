package org.thshsh.crypt.web.view;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.cryptman.Allocation;
import org.thshsh.cryptman.AllocationRepository;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder.Binding;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class AllocationForm extends EntityForm<Allocation, Long> {

	@Autowired
	ApplicationContext context;

	@Autowired
	AllocationRepository alloRepo;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	@Qualifier("hundred")
	BigDecimal hundred;

	Portfolio portfolio;
	Currency currency;
	Binding<?,?> percentBinding;


	public AllocationForm(Allocation entity) {
		super(Allocation.class, entity);
	}

	public AllocationForm(Allocation entity,Portfolio p,Currency c) {
		super(Allocation.class, entity);
		this.portfolio=p;
		this.currency = c;
	}

	public AllocationForm(Portfolio p,Currency c) {
		super(Allocation.class, null);
		this.portfolio=p;
		this.currency = c;
	}

	@Override
	protected JpaRepository<Allocation, Long> getRepository() {
		return alloRepo;
	}

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

		Checkbox undef = new Checkbox("Undefined");
		formLayout.add(undef);
		undef.addValueChangeListener(change -> {
			if(change.getValue()) {
				percent.setEnabled(false);
				percent.setValue(null);
				percentBinding.setAsRequiredEnabled(false);
			}
			else {
				percent.setEnabled(true);
			}
		});

		formLayout.endLayout();
		formLayout.startHorizontalLayout();


		ComboBox<Currency> ass = new ComboBox<>("Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");

		if(currency==null)formLayout.add(ass);

		binder.forField(ass)
		.asRequired()
		.bind(Allocation::getCurrency, Allocation::setCurrency);



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



		percentBinding = binder.forField(percent)
		.asRequired()
		.withConverter(v -> {
			if(v == null) return null;
			else return v.divide(hundred);
		}, v -> {
			if(v == null) return null;
			else return v.multiply(hundred);
		})

		.bind(Allocation::getPercent, Allocation::setPercent);


	}



	@Override
	protected void persist() {
		LOGGER.info("persisting: {}",this.entity);
		super.persist();
	}

	protected Allocation createEntity() {
		LOGGER.info("createEntity: {}",this.portfolio);
		Allocation b = super.createEntity();
		if(portfolio!=null) b.setPortfolio(portfolio);
		if(currency!=null) b.setCurrency(currency);
		return b;
	}

	@Override
	protected Long getEntityId(Allocation e) {
		return e.getId();
	}

}