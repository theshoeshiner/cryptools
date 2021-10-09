package org.thshsh.crypt.web.view;

import java.util.Collections;

import org.apache.commons.collections4.map.SingletonMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.serv.PortfolioHistoryService;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.SecurityUtils;
import org.thshsh.crypt.web.UsernameEmailDataProvider;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioSettings;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioForm extends EntityForm<Portfolio, Long> {

	@Autowired
	PortfolioHistoryService historyService;
	
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
		this.createText = "New";
		this.saveText = "Save and Open";
		this.confirm = false;
	}

	@Override
	protected JpaRepository<Portfolio, Long> getRepository() {
		return portRepo;
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

		ComboBox<Currency> ass = new ComboBox<>("Reserve Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(appContext.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");
		binder.forField(ass).asRequired().bind(
				FunctionUtils.nestedValue(Portfolio::getSettings,PortfolioSettings::getReserve),
				FunctionUtils.nestedSetter(Portfolio::getSettings, PortfolioSettings::setReserve)
				);
		formLayout.add(ass);
		
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

		formLayout.endLayout();

	}
	
	

	@Override
	protected void save() throws ValidationException {
		super.save();
	}
	
	@Override
	protected void persist()  {
		super.persist();
		historyService.runHistoryJob(entity);
		UI.getCurrent().navigate("portfolio", QueryParameters.simple(new SingletonMap<>("id",entity.getId().toString())));
	}	

	@Override
	protected Long getEntityId(Portfolio e) {
		return e.getId();
	}

	@Override
	protected Portfolio createEntity() {
		Portfolio p = super.createEntity();
		p.getSettings().setReserve(assetRepo.findByKey("USD"));
		p.setUser(session.getUser());
		return p;
	}



}
