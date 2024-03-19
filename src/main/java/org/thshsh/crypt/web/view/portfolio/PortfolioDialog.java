package org.thshsh.crypt.web.view.portfolio;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Portfolio;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioDialog extends org.thshsh.vaadin.entity.EntityDialog<Portfolio,Long> {

	public PortfolioDialog() {
		super(PortfolioForm.class);
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
