package org.thshsh.crypt.web.view;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.cryptman.BigDecimalConverter;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioRepository;
import org.thshsh.cryptman.PortfolioSettings;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioSettingsForm extends EntityForm<PortfolioSettings, Serializable> {

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

	@Override
	protected JpaRepository<PortfolioSettings, Serializable> getRepository() {
		return null;
	}

	@Override
	protected void setupForm() {

		this.title.setVisible(false);

		ComboBox<Currency> ass = new ComboBox<>("Reserve Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(appContext.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");
		binder.forField(ass).asRequired().bind(
				PortfolioSettings::getReserve,PortfolioSettings::setReserve
				//FunctionUtils.nestedValue(Portfolio::getSettings,PortfolioSettings::getReserve),
				//FunctionUtils.nestedSetter(Portfolio::getSettings, PortfolioSettings::setReserve)
				);


		TextField balance = new TextField("Minimum Adjustment");
		binder.forField(balance)
		.asRequired()
		.withNullRepresentation("")
		.withConverter(new BigDecimalConverter())
		.bind(PortfolioSettings::getMinimumAdjust, PortfolioSettings::setMinimumAdjust);
		
		Checkbox alerts = new Checkbox("Disable Alerts");
		binder.forField(alerts).bind(PortfolioSettings::getAlertsDisabled,PortfolioSettings::setAlertsDisabled);


		formLayout.startVerticalLayout();
		//formLayout.add(nameField);
		formLayout.add(ass);
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
	}



}
