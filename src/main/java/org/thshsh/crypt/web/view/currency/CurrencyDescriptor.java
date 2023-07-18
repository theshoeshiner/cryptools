package org.thshsh.crypt.web.view.currency;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.web.view.AppEntityDescriptor;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CurrencyDescriptor extends AppEntityDescriptor<Currency>{

	public CurrencyDescriptor() {
		super(Currency.class);
	}

}