package org.thshsh.crypt.web.view;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Exchange;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ExchangeDialog extends org.thshsh.vaadin.entity.EntityDialog<Exchange,Long> {

	public ExchangeDialog(Exchange entity) {
		super(ExchangeForm.class, entity);
	}


/*	@Autowired
	ExchangeRepository repo;

	public ExchangeDialog(Exchange en) {
		super(en,Exchange.class);
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct(repo);
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
*/



}
