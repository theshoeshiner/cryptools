package org.thshsh.crypt.web.view.allocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.serv.BalanceService;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.crypt.web.view.PercentConverter;
import org.thshsh.crypt.web.view.manage.PortfolioEntryGrid;
import org.thshsh.vaadin.SingleCheckboxGroup;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@CssImport("./styles/allocation-form.css")
public class AllocationForm extends AppEntityForm<Allocation, Long> {

	@Autowired
	ApplicationContext context;

	@Autowired
	AllocationRepository alloRepo;

	@Autowired
	CurrencyRepository assetRepo;
	
	@Autowired
	BalanceService balServ;
	
	@Autowired
	ManagePortfolioService manageServ;

	@Autowired
	@Qualifier("hundred")
	BigDecimal hundred;

	Portfolio portfolio;
	Currency currency;
	Binding<?,?> percentBinding;
	
	@Autowired
	ImageService imageService;

	BigDecimal max;
	String maxString;
	SingleCheckboxGroup undefinedCheckbox;
	SingleCheckboxGroup detectField;
	


	public AllocationForm(Allocation entity,Portfolio p,Currency c) {
		super(entity);
		this.portfolio=p;
		this.currency = c;
	}





	@Override
	public void postConstruct() {
		super.postConstruct();
		this.getButtons().setConfirm(false);
	}





	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Allocation, Long> descriptor) {
		super.setDescriptor(descriptor);
	}



	@Override
	@Autowired
	public void setRepository(CrudRepository<Allocation, Long> repository) {
		super.setRepository(repository);
	}





	protected String getColor() {
		LOGGER.info("get color c: {} e: {}",this.currency,this.entity);
		if(this.currency != null) return this.currency.getColorHex();
		else if(this.entity.getCurrency() != null) return this.entity.getCurrency().getColorHex();
		else return null;
	}

	@Override
	protected void setupForm() {
		
		this.setWidthFull();
		
		if(currency != null) {
			
			this.titleLayout.removeAll();
			

			Image img = new Image(imageService.getImageUrl(currency),""); 
			img.addClassName("allocation-icon");
			titleLayout.add(img);
			String color = getColor();
			
			//Span key = new Span(this.currency.getKey());
			if(color != null) {
				titleLayout.getElement().getStyle().set("color", "#"+color);
			}
			//header.add(key);
			Div name = new Div();
			name.add(new Span(this.currency.getName()));
			name.add(new Span("Allocation"));
			//Span name = ;
			titleLayout.add(name);
			
			//replace(getTitle(), header);
			//titleLayout.add(header);
			
		}
		
		
		
		formLayout.getLayout().setPadding(false);

		
		BigDecimal sum = alloRepo.findAllocationSumByPortfolio(portfolio).orElseGet(() -> BigDecimal.ZERO);
		max = BigDecimal.ONE.subtract(sum);
		if(entity.getPercent()!=null) max = max.add(entity.getPercent());
		
		//BigDecimal remainder = portfolio.getAllocationRemainder();
		maxString = PortfolioEntryGrid.PercentFormat.format(max);
		
		BigDecimalField percent = new BigDecimalField("Allocation 0-"+maxString+"");
		percent.setWidth("150px");
		formLayout.add(percent);
		
		

		undefinedCheckbox = new SingleCheckboxGroup("Remainder");
		undefinedCheckbox.setHelperText("Leftover unallocated percent will be assigned to this currency.");
		formLayout.add(undefinedCheckbox);
		undefinedCheckbox.addValueChangeListener(change -> {
			if(change.isFromClient()) {
				percentBinding.setAsRequiredEnabled(!change.getValue());
				if(change.getValue()) {
					detectField.setValue(false);
					percent.setValue(null);
					percentBinding.setAsRequiredEnabled(false);
				}
			}
		});
		binder
		.forField(undefinedCheckbox)
		.bind(Allocation::getUndefined, Allocation::setUndefined);
	
		
		detectField = new SingleCheckboxGroup("Detect & Adjust");
		detectField.setHelperText("Automatically detects and sets allocation for this Currency. Beware this may override other allocations.");
		formLayout.add(detectField);
		detectField.addValueChangeListener(change -> {
			if(change.isFromClient()) {
				percentBinding.setAsRequiredEnabled(!change.getValue());
				if(change.getValue()) {
					percent.setValue(null);
					undefinedCheckbox.setValue(false);
				}
			}
		});
		
		


		percent.addValueChangeListener(change -> {
			if(change.isFromClient()) {
				LOGGER.info("value: {}",change.getValue());
				if(change.getValue()!=null) {
					detectField.setValue(false);
					undefinedCheckbox.setValue(false);
				}
			}
		});

		percentBinding = binder.forField(percent)
		.asRequired()
		
		.withConverter(new Converter<BigDecimal,BigDecimal>() {

			
			@Override
			public Result<BigDecimal> convertToModel(BigDecimal value, ValueContext context) {
				if(value == null) return Result.ok(null);
				return Result.ok(value);
			}

			@Override
			public BigDecimal convertToPresentation(BigDecimal value, ValueContext context) {
				if(value == null) return  null;
				return value.setScale(2, RoundingMode.HALF_EVEN);
			}

		
			
		})
		.withValidator((value,context) -> {
			if(value == null) return ValidationResult.ok();
			value = value.divide(hundred);
			if(value.compareTo(max) > 0) {
				return ValidationResult.error("Must be < "+maxString);
			}
			else if(value.compareTo(BigDecimal.ZERO) < 0) {
				return ValidationResult.error("Must be > 0");
			}
			
			return ValidationResult.ok();
		})
				
		.withConverter(new PercentConverter())

		.bind(Allocation::getPercent, Allocation::setPercent);


	}



	@Override
	protected Allocation persist() {
		Allocation a = super.persist();
		if(detectField.isTrue()) {
			PortfolioHistory h = manageServ.createHistory(portfolio, false);
			balServ.autoDetectAllocation(currency, portfolio, h);
		}
		return a;
	}

	protected Allocation createEntity() {
		Allocation b = super.createEntity();
		if(portfolio!=null) b.setPortfolio(portfolio);
		if(currency!=null) b.setCurrency(currency);
		return b;
	}



}
