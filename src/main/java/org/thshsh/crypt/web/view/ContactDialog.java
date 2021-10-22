package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Contact;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ContactDialog extends AppEntityDialog<Contact> {

	public ContactDialog(Contact c) {
		super(ContactForm.class, c);
	}
	
	public ContactDialog() {
		super(ContactForm.class, null);
	}
	
	@PostConstruct
	@Override
	public void postConstruct() {
		this.setWidth("500px");
		super.postConstruct();
		this.setCloseOnOutsideClick(true);
		
	}

}
