package org.thshsh.crypt.web.view.portfolio;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.PortfolioAlert;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PortfolioAlertDescriptor extends AppEntityDescriptor<PortfolioAlert>{

	public PortfolioAlertDescriptor() {
		super(PortfolioAlert.class);
	}

}
