package org.thshsh.crypt.web.view.allocation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.web.view.AppEntityDescriptor;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AllocationDescriptor extends AppEntityDescriptor<Allocation>{

	public AllocationDescriptor() {
		super(Allocation.class);
	}

}
