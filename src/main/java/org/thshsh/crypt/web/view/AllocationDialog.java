package org.thshsh.crypt.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;



@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class AllocationDialog extends org.thshsh.vaadin.entity.EntityDialog<Allocation, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(AllocationDialog.class);
	
	@Autowired
	ImageService imageService;

	/*
	@Autowired
	ApplicationContext context;

	//@Autowired
	//AssetNameDataProvider assetNamePro;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	ExchangeRepository exeRepo;


	@Autowired
	AllocationRepository alloRepo;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	AppSession session;



	@Autowired
	@Qualifier("hundred")
	BigDecimal hundred;

	*/

	Portfolio portfolio;
	Currency currency;

	/*public AllocationDialog(Portfolio p,Currency c) {
		super(AllocationForm.class,null);
		this.portfolio = p;
		this.currency = c;
	}*/

	public AllocationDialog(Allocation a,Portfolio p,Currency c) {
		super(AllocationForm.class,a);
		this.portfolio = p;
		this.currency = c;
	}

	/*public AllocationDialog(Portfolio p) {
		super(AllocationForm.class,null);
		this.portfolio = p;
	}*/

	@Override
	protected EntityForm<Allocation, Long> createEntityForm() {
		//AllocationForm af;
		if(this.entity == null) return this.appContext.getBean(AllocationForm.class,null,this.portfolio,this.currency);
		else return this.appContext.getBean(AllocationForm.class,this.entity,this.portfolio,this.currency);
	}

	protected String getColor() {
		LOGGER.info("get color c: {} e: {}",this.currency,this.entity);
		if(this.currency != null) return this.currency.getColorHex();
		else if(this.entity.getCurrency() != null) return this.entity.getCurrency().getColorHex();
		else return null;
	}
	
	public void postConstruct() {
		
		super.postConstruct();
		this.setCloseOnEsc(true);
		this.setCloseOnOutsideClick(true);
		
		if(currency != null) {
			
			HorizontalLayout header = new HorizontalLayout();
			
			//this.entityForm.getTitle().removeAll();
			//this.entityForm.getTitle().add(new Text("Allocation for "));
			Image img = new Image(imageService.getImageUrl(currency),""); 
			img.addClassName("allocation-icon");
			header.add(img);
			String color = getColor();
			
			Text key = new Text(this.currency.getKey());
			if(color != null) {
				this.entityForm.getTitle().getElement().getStyle().set("color", "#"+color);
			}
			header.add(key);
			Span name = new Span(this.currency.getName());
			header.add(name);
			
			this.entityForm.replace(this.entityForm.getTitle(), header);
			
			
		}
		else {
			this.entityForm.getTitle().setText("Allocation"+((this.currency!=null)?" for "+this.currency.getKey():""));
		}
	}

	/*public AllocationDialog(Allocation en,Portfolio p,Currency c) {
		super(en, Allocation.class);
		this.portfolio = p;
		this.currency = c;
	}
	*/



	/*@Override
	protected Allocation createEntity() {
		LOGGER.info("createEntity: {}",this.portfolio);
		Allocation b = super.createEntity();
		if(portfolio!=null) b.setPortfolio(portfolio);
		if(currency!=null) b.setCurrency(currency);
		return b;
	}*/



	/*public BalanceDialog(Balance en) {
		super(en, Balance.class);
	}*/

	/*@PostConstruct
	public void postConstruct() {
		super.postConstruct(alloRepo);
		this.title.setVisible(false);
	}

	ComboBox<Exchange> exchangeField;

	@SuppressWarnings("unchecked")
	@Override
	protected void setupForm() {

		formLayout.startHorizontalLayout();



		BigDecimalField percent = new BigDecimalField("Percent");

		formLayout.add(percent);

		formLayout.endLayout();
		formLayout.startHorizontalLayout();

		if(currency == null) {
			ComboBox<Currency> ass = new ComboBox<>("Currency");
			ass.setItemLabelGenerator(c -> {
				return c.getName() +" ("+c.getKey()+")";
			});
			ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
			ass.setWidth("250px");
			formLayout.add(ass);

			binder.forField(ass)
			.asRequired()
			.bind(Allocation::getCurrency, Allocation::setCurrency);
		}


		formLayout.endLayout();


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
	}*/

}
