package org.thshsh.crypt.web.view.exchange;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ExchangeDescriptor extends AppEntityDescriptor<Exchange>{

	public ExchangeDescriptor() {
		super(Exchange.class);
	}

}
