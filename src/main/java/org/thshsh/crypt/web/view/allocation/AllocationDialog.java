package org.thshsh.crypt.web.view.allocation;

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



@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class AllocationDialog extends org.thshsh.vaadin.entity.EntityDialog<Allocation, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(AllocationDialog.class);
	
	@Autowired
	ImageService imageService;


	Portfolio portfolio;
	Currency currency;

	public AllocationDialog(Portfolio p,Currency c) {
		super(AllocationForm.class,null);
		this.portfolio = p;
		this.currency = c;
		
	}
	

	public AllocationDialog(Allocation a) {
		super(AllocationForm.class,a);
		this.portfolio = a.getPortfolio();
		this.currency = a.getCurrency();
	}

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
		this.setWidth("350px");
		this.setCloseOnEsc(true);
		this.setCloseOnOutsideClick(true);

	}


}
