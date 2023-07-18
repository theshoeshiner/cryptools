package org.thshsh.crypt.web.view.balance;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BalanceDescriptor extends AppEntityDescriptor<Balance>{

	public BalanceDescriptor() {
		super(Balance.class);
	}

}
