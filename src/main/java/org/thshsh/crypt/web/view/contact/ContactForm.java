package org.thshsh.crypt.web.view.contact;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.ContactType;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ContactForm extends AppEntityForm<Contact,Long>{

	
	public ContactForm() {
		super(null);
	}
	
	public ContactForm(Contact entity) {
		super(entity);
	}


	
	@PostConstruct
	@Override
	public void postConstruct() {
		this.loadFromId=false;
		//this.confirm = false;
		super.postConstruct();
		//this.cancel.setVisible(false);
		
		this.getButtons().getSave().setText("Submit");
		this.getButtons().setConfirm(false);
		this.getButtons().getCancel().setVisible(false);
		
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
	@Autowired
	public void setDescriptor(EntityDescriptor<Contact, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(CrudRepository<Contact, Long> repository) {
		super.setRepository(repository);
	}

	
}
