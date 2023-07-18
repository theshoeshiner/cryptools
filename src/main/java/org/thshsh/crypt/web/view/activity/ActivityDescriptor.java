package org.thshsh.crypt.web.view.activity;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ActivityDescriptor extends AppEntityDescriptor<Activity>{

	public ActivityDescriptor() {
		super(Activity.class);
	}

}
