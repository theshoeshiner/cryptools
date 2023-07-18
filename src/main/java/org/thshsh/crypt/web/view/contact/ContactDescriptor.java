package org.thshsh.crypt.web.view.contact;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.web.view.AppEntityDescriptor;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ContactDescriptor extends AppEntityDescriptor<Contact>{

	public ContactDescriptor() {
		super(Contact.class);
	}

}