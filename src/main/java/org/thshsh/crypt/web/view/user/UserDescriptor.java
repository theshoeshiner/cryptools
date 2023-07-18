package org.thshsh.crypt.web.view.user;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserDescriptor extends AppEntityDescriptor<User>{

	public UserDescriptor() {
		super(User.class);
	}

}
