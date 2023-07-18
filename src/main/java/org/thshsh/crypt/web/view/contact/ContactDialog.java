package org.thshsh.crypt.web.view.contact;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.web.view.AppEntityDialog;

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
