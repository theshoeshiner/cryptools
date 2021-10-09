package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.cryptman.Portfolio;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioDialog extends org.thshsh.vaadin.entity.EntityDialog<Portfolio,Long> {

	public PortfolioDialog() {
		super(PortfolioForm.class,null);
	}
	
	public PortfolioDialog(Portfolio en) {
		super(PortfolioForm.class,en);
		
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.setCloseOnOutsideClick(true);
	}

}
