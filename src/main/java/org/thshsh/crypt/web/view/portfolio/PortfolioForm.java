package org.thshsh.crypt.web.view.portfolio;

import java.math.BigDecimal;

import org.apache.commons.collections4.map.SingletonMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioSettings;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.serv.PortfolioHistoryService;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.UsernameEmailDataProvider;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.crypt.web.view.currency.CurrencyDataProvider;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.QueryParameters;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioForm extends AppEntityForm<Portfolio, Long> {

	BigDecimal DEFAULT_IND_THRESH = new BigDecimal(".15");
	BigDecimal DEFAULT_PORT_THRESH = new BigDecimal(".05");
	
	@Autowired
	PortfolioHistoryService historyService;
	
	@Autowired
	ApplicationContext appContext;


	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	AppSession session;

	public PortfolioForm(Portfolio entity) {
		super(entity);
		this.createText = "New";
	}

	
	

	@Override
	public void postConstruct() {
		super.postConstruct();
		this.getButtons().setConfirm(false);
		this.getButtons().getSave().setText("Save and Open");
	}




	@Override
	protected void setupForm() {

		formLayout.startVerticalLayout();

		TextField nameField = new TextField("Name");
		nameField.setWidth("200px");
		binder.forField(nameField)
		.asRequired()
		.withValidator(new StringLengthValidator("", 3, 64))
		.bind(Portfolio::getName, Portfolio::setName);
		formLayout.add(nameField);

		ComboBox<Currency> reserveField = new ComboBox<>("Reserve Currency");
		reserveField.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		reserveField.setReadOnly(true);
		reserveField.setItems(appContext.getBean(CurrencyDataProvider.class));
		reserveField.setWidth("250px");
		binder.forField(reserveField).asRequired().bind(
				BinderUtils.nestedValue(Portfolio::getSettings,PortfolioSettings::getReserve),
				BinderUtils.nestedSetter(Portfolio::getSettings, PortfolioSettings::setReserve)
				);
		formLayout.add(reserveField);
		
		if(SecurityUtils.hasAccess(Feature.User, Access.ReadWrite)) {

			ComboBox<User> user = new ComboBox<>("User");
			user.setItemLabelGenerator(c -> {
				return c.getUserName() +" ("+c.getEmail()+")";
			});
			user.setItems(appContext.getBean(UsernameEmailDataProvider.class));
			user.setWidth("300px");
			binder.forField(user).asRequired().bind(
					Portfolio::getUser,Portfolio::setUser
					);
			
			formLayout.add(user);

		}

		formLayout.endComponent();

	}
	
	

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Portfolio, Long> descriptor) {
		super.setDescriptor(descriptor);
	}


	@Override
	@Autowired
	public void setRepository(CrudRepository<Portfolio, Long> repository) {
		super.setRepository(repository);
	}


	@Override
	protected void save() throws ValidationException {
		super.save();
	}
	
	@Override
	protected Portfolio persist()  {
		Portfolio p = super.persist();
		historyService.runHistoryJob(entity);
		UI.getCurrent().navigate("portfolio", QueryParameters.simple(new SingletonMap<>("id",entity.getId().toString())));
		return p;
	}	



	@Override
	protected Portfolio createEntity() {
		Portfolio p = super.createEntity();
		p.getSettings().setReserve(assetRepo.findByKey("USD"));
		p.setUser(session.getUser());
		p.getSettings().setIndividualThreshold(DEFAULT_IND_THRESH);
		p.getSettings().setPortfolioThreshold(DEFAULT_PORT_THRESH);
		return p;

	}



}
