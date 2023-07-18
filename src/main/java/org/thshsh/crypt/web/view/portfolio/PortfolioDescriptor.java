package org.thshsh.crypt.web.view.portfolio;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.web.view.AppEntityDescriptor;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PortfolioDescriptor extends AppEntityDescriptor<Portfolio>{

	public PortfolioDescriptor() {
		super(Portfolio.class);
	}

}

