package org.thshsh.crypt.web.view.portfolio;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioSettings;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.crypt.web.view.PercentConverter;
import org.thshsh.crypt.web.view.currency.CurrencyDataProvider;
import org.thshsh.vaadin.DurationField;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.BigDecimalField;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioSettingsForm extends AppEntityForm<PortfolioSettings, Integer> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioSettingsForm.class);

	@Autowired
	ApplicationContext appContext;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	AppSession session;

	ManagePortfolioView manageView;
	
	Portfolio portfolio;

	public PortfolioSettingsForm(ManagePortfolioView v, Portfolio entity) {
		super(entity.getSettings());
		this.portfolio = entity;
		this.manageView = v;
	}
	
	@PostConstruct
	public void postConstruct() {
		
		super.postConstruct();
		this.setPadding(false);
		this.setSpacing(false);
		this.setMargin(false);
		this.formLayout.setPadding(false);
		this.formLayout.setMargin(false);
		this.getButtons().setDisableSaveUntilChange(true);
		this.getButtons().getCancel().setVisible(false);
		
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
				//BinderUtils.nestedValue(Portfolio::getSettings,PortfolioSettings::getReserve),
				//BinderUtils.nestedSetter(Portfolio::getSettings, PortfolioSettings::setReserve)
				);


		BigDecimalField minimumAdjustField = new BigDecimalField("Minimum Adjust "+this.entity.getReserve().getKey());
		minimumAdjustField.setWidth("150px");
		
		binder
		.forField(minimumAdjustField)
		.bind(PortfolioSettings::getMinimumAdjust, PortfolioSettings::setMinimumAdjust);
		
		BigDecimalField indThreshField = new BigDecimalField("Individual Threshold %");
		indThreshField.setWidth("150px");
		
		binder
		.forField(indThreshField)
		.withConverter(new PercentConverter())
		.bind(PortfolioSettings::getIndividualThreshold, PortfolioSettings::setIndividualThreshold);
		
		BigDecimalField portThreshField = new BigDecimalField("Portfolio Threshold %");
		portThreshField.setWidth("150px");
		
		binder
		.forField(portThreshField)
		.withConverter(new PercentConverter())
		.bind(PortfolioSettings::getPortfolioThreshold, PortfolioSettings::setPortfolioThreshold);
		
		//CheckboxGroup<?> group = new CheckboxGroup<?>();
		
		Checkbox alerts = new Checkbox("Disable Alerts");
		binder.forField(alerts).bind(PortfolioSettings::getAlertsDisabled,PortfolioSettings::setAlertsDisabled);
		alerts.addClassName("vertical");

		DurationField silentField = new DurationField("Alerts Muted Until");
		binder.forField(silentField).bind(PortfolioSettings::getSilentTillDuration, PortfolioSettings::setSilentTillDuration);
		
		/*TextField silentField = new TextField("Silent Till");
		silentField.setReadOnly(true);
		if(entity.getSilentTill()!=null) {
			PrettyTime prettyTime = new PrettyTime(LocalDateTime.now());
			String format = prettyTime.format(entity.getSilentTill());
			silentField.setValue(format);
		}*/
		
		formLayout.startVerticalLayout();
		//formLayout.add(nameField);
		formLayout.add(reserveField);
		formLayout.startHorizontalLayout();
		formLayout.add(minimumAdjustField);
		formLayout.add(portThreshField);
		formLayout.add(indThreshField);
		formLayout.endComponent();
		formLayout.add(silentField);
		formLayout.add(alerts);
		formLayout.endComponent();
		
		
		


	}


	@Override
	protected PortfolioSettings persist() {
		LOGGER.info("save");
		Portfolio p = portRepo.findById(portfolio.getId()).get();
		p.setSettings(entity);
		portRepo.save(p);
		Notification n = Notification.show("Saved", 500, Position.MIDDLE);
		if(manageView != null) manageView.refreshValueRelatedTabs();
		//Notification n = new Notification("Saved", 500, Position.MIDDLE);
		
		//Notification.show("", 500, Position.MIDDLE);
		return entity;
	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<PortfolioSettings, Integer> descriptor) {
		super.setDescriptor(descriptor);
	}


	
}
