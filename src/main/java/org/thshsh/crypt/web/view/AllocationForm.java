package org.thshsh.crypt.web.view;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder.Binding;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@CssImport("./styles/allocation-form.css")
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


	/*public AllocationForm(Allocation entity) {
		super(Allocation.class, entity);
	}*/

	public AllocationForm(Allocation entity,Portfolio p,Currency c) {
		super(Allocation.class, entity);
		this.portfolio=p;
		this.currency = c;
	}

	/*public AllocationForm(Portfolio p,Currency c) {
		super(Allocation.class, null);
		this.portfolio=p;
		this.currency = c;
	}*/

	@Override
	protected JpaRepository<Allocation, Long> getRepository() {
		return alloRepo;
	}
	
	protected String getColor() {
		LOGGER.info("get color c: {} e: {}",this.currency,this.entity);
		if(this.currency != null) return this.currency.getColorHex();
		else if(this.entity.getCurrency() != null) return this.entity.getCurrency().getColorHex();
		else return null;
	}

	@Override
	protected void setupForm() {
		
		formLayout.setPadding(false);
		
		Span info = new Span(this.entity.getCurrency().getName());
		info.addClassName("helper-text");
		//
		String color = getColor();
		
		if(color != null) {
			info.getElement().getStyle().set("color", "#"+color);
		}
		formLayout.add(info);

		HorizontalLayout row = formLayout.startHorizontalLayout();

		/*exchangeField = new ComboBox<>("Exchange");
		exchangeField.setItemLabelGenerator(Exchange::getName);
		exchangeField.setItems(context.getBean(HasNameDataProvider.class,exeRepo));
		exchangeField.setWidth("250px");
		formLayout.add(exchangeField);*/

		//row.setAlignItems(Alignment.CENTER);
		
		
		BigDecimalField percent = new BigDecimalField("Percent");
		formLayout.add(percent);

		Checkbox undef = new Checkbox("Auto Allocate");
		formLayout.add(undef);
		undef.addValueChangeListener(change -> {
			if(change.getValue()) {
				percent.setReadOnly(true);
				percent.setValue(null);
				percentBinding.setAsRequiredEnabled(false);
			}
			else {
				percent.setReadOnly(false);
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

		if(entity.isUndefined()) {
			undef.setValue(true);
		}
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
