package org.thshsh.crypt.web.view.rate;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MarketRateDescriptor extends AppEntityDescriptor<MarketRate>{

	public MarketRateDescriptor() {
		super(MarketRate.class);
	}

}
