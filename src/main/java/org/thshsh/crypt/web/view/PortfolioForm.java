package org.thshsh.crypt.web.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioRepository;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioForm extends EntityForm<Portfolio, Long> {

	@Autowired
	ApplicationContext appContext;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	AppSession session;

	public PortfolioForm(Portfolio entity) {
		super(Portfolio.class, entity);
	}

	@Override
	protected JpaRepository<Portfolio, Long> getRepository() {
		return portRepo;
	}

	@Override
	protected void setupForm() {



		TextField nameField = new TextField("Name");
		nameField.setWidth("200px");
		binder.forField(nameField)
		.asRequired()
		.withValidator(new StringLengthValidator("", 3, 64))
		.bind(Portfolio::getName, Portfolio::setName);

		ComboBox<Currency> ass = new ComboBox<>("Reserve Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(appContext.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");
		binder.forField(ass).asRequired().bind(Portfolio::getReserve, Portfolio::setReserve);


		formLayout.startVerticalLayout();
		formLayout.add(nameField);
		formLayout.add(ass);
		formLayout.endLayout();

	}

	@Override
	protected Long getEntityId(Portfolio e) {
		return e.getId();
	}

	@Override
	protected Portfolio createEntity() {
		Portfolio p = super.createEntity();
		p.setReserve(assetRepo.findByKey("USD"));
		p.setUser(session.getUser());
		return p;
	}



}
