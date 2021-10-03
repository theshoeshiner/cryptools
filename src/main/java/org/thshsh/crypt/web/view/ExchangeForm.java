package org.thshsh.crypt.web.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.textfield.TextField;

public class ExchangeForm extends EntityForm<Exchange,Long> {

	@Autowired
	ExchangeRepository repo;
	
	public ExchangeForm(Exchange entity) {
		super(Exchange.class, entity);
	}

	@Override
	protected JpaRepository<Exchange, Long> getRepository() {
		return repo;
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
		formLayout.endLayout();

		formLayout.startHorizontalLayout();
		formLayout.add(remname);
		formLayout.add(remid);
		formLayout.endLayout();

		formLayout.startHorizontalLayout();
		formLayout.add(image);
		formLayout.endLayout();

		
	}

	@Override
	protected Long getEntityId(Exchange e) {
		return e.getId();
	}

}
