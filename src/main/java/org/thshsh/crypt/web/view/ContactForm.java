package org.thshsh.crypt.web.view;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.ContactType;
import org.thshsh.crypt.repo.ContactRepository;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ContactForm extends AppEntityForm<Contact,Long>{

	@Autowired
	ContactRepository repo;
	
	public ContactForm() {
		super(Contact.class, null);
	}
	
	public ContactForm(Contact entity) {
		super(Contact.class, entity);
	}

	@Override
	protected JpaRepository<Contact, Long> getRepository() {
		return repo;
	}

	
	@PostConstruct
	@Override
	public void postConstruct() {
		this.loadFromId=false;
		this.confirm = false;
		this.saveText = "Submit";
		super.postConstruct();
		this.cancel.setVisible(false);
	}
	
	@Override
	protected void setupForm() {
		
		this.titleLayout.removeAll();
		
		ComboBox<ContactType> type = new ComboBox<ContactType>("Reason");
		type.setWidthFull();
		type.setItems(Arrays.asList(ContactType.values()));
		formLayout.add(type);
		
		binder.forField(type).asRequired().bind(Contact::getType, Contact::setType);
		
		TextArea text = new TextArea();
		text.setWidthFull();
		text.setMaxLength(2000);
		text.setHeight("150px");
		formLayout.add(text);
		
		binder.forField(text).asRequired().bind(Contact::getText,Contact::setText);
		
	}

	@Override
	protected Long getEntityId(Contact e) {
		return e.getId();
	}

}
