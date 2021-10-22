package org.thshsh.crypt.web.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.BigDecimalConverter;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioSettings;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioSettingsForm extends AppEntityForm<PortfolioSettings, Serializable> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioSettingsForm.class);

	@Autowired
	ApplicationContext appContext;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	AppSession session;

	Portfolio portfolio;

	public PortfolioSettingsForm(Portfolio entity) {
		super(PortfolioSettings.class, entity.getSettings());
		this.portfolio = entity;
	}
	
	@PostConstruct
	public void postConstruct() {
		this.disableSaveUntilChange=true;
		super.postConstruct();
	}

	@Override
	protected JpaRepository<PortfolioSettings, Serializable> getRepository() {
		return null;
	}

	@Override
	protected void setupForm() {

		this.title.setVisible(false);

		ComboBox<Currency> reserveField = new ComboBox<>("Reserve Currency");
		reserveField.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		reserveField.setReadOnly(true);
		reserveField.setItems(appContext.getBean(CurrencyDataProvider.class));
		reserveField.setWidth("250px");
		binder
			.forField(reserveField)
			.asRequired()
			.bind(
				PortfolioSettings::getReserve,PortfolioSettings::setReserve
				//FunctionUtils.nestedValue(Portfolio::getSettings,PortfolioSettings::getReserve),
				//FunctionUtils.nestedSetter(Portfolio::getSettings, PortfolioSettings::setReserve)
				);


		TextField balance = new TextField("Minimum Adjustment");
		binder
			.forField(balance)
		//.asRequired()
			.withNullRepresentation(StringUtils.EMPTY)
			.withConverter(new BigDecimalConverter())
			.bind(PortfolioSettings::getMinimumAdjust, PortfolioSettings::setMinimumAdjust);
		
		Checkbox alerts = new Checkbox("Disable Alerts");
		binder.forField(alerts).bind(PortfolioSettings::getAlertsDisabled,PortfolioSettings::setAlertsDisabled);

		
		formLayout.startVerticalLayout();
		//formLayout.add(nameField);
		formLayout.add(reserveField);
		formLayout.add(balance);
		formLayout.add(alerts);
		formLayout.endLayout();
		
		
		


	}

	@Override
	protected Serializable getEntityId(PortfolioSettings e) {
		return null;
	}


	@Override
	protected void persist() {
		LOGGER.info("save");
		Portfolio p = portRepo.findById(portfolio.getId()).get();
		p.setSettings(entity);
		portRepo.save(p);
		Notification n = Notification.show("Saved", 500, Position.MIDDLE);
		//Notification n = new Notification("Saved", 500, Position.MIDDLE);
		
		//Notification.show("", 500, Position.MIDDLE);
	}



}
