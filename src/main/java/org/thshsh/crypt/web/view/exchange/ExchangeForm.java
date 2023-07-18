package org.thshsh.crypt.web.view.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Grade;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ExchangeForm extends AppEntityForm<Exchange,Long> {


	public ExchangeForm(Exchange entity) {
		super(entity);
	}


	@Override
	protected void setupForm() {
		

		TextField name = new TextField("Name");
		binder
		.forField(name)
		.asRequired()
		.bind(Exchange::getName, Exchange::setName);

		TextField key = new TextField("Key");
		binder
		.forField(key)
		.asRequired()
		.bind(Exchange::getKey, Exchange::setKey);
		
		ComboBox<Grade> grade = new ComboBox<>("Grade");
		grade.setItems(Grade.values());
		binder
		.forField(grade)
		.bind(Exchange::getGrade, Exchange::setGrade);

		TextField remname = new TextField("Remote Name");
		binder
		.forField(remname)
		.bind(Exchange::getRemoteName, Exchange::setRemoteName);

		TextField remid = new TextField("Remote Id");
		binder
		.forField(remid)
		.bind(Exchange::getRemoteId, Exchange::setRemoteId);

		TextField image = new TextField("Image");
		binder
		.forField(image)
		.bind(Exchange::getImageUrl, Exchange::setImageUrl);

		formLayout.startHorizontalLayout();
		formLayout.add(name);
		formLayout.add(key);
		formLayout.endComponent();
		
		formLayout.startHorizontalLayout();
		formLayout.add(grade);
		formLayout.endComponent();

		formLayout.startHorizontalLayout();
		formLayout.add(remname);
		formLayout.add(remid);
		formLayout.endComponent();

		formLayout.startHorizontalLayout();
		formLayout.add(image);
		formLayout.endComponent();

		
	}


	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Exchange, Long> descriptor) {
		super.setDescriptor(descriptor);
	}


	@Override
	@Autowired
	public void setRepository(CrudRepository<Exchange, Long> repository) {
		super.setRepository(repository);
	}



}
