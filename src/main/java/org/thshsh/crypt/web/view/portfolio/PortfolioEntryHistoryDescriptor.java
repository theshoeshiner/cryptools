package org.thshsh.crypt.web.view.portfolio;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PortfolioEntryHistoryDescriptor extends AppEntityDescriptor<PortfolioEntryHistory>{

	public PortfolioEntryHistoryDescriptor() {
		super(PortfolioEntryHistory.class);
	}

}

