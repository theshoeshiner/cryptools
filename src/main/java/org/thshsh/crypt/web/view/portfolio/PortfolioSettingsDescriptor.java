package org.thshsh.crypt.web.view.portfolio;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.PortfolioSettings;
import org.thshsh.vaadin.entity.EntityDescriptor;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PortfolioSettingsDescriptor extends EntityDescriptor<PortfolioSettings,Integer>{

	public PortfolioSettingsDescriptor() {
		super(PortfolioSettings.class);
	}

	@Override
	public Integer getEntityId(PortfolioSettings e) {
		return e.hashCode();
	}
	
	

}
