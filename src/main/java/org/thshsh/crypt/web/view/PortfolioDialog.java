package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.CurrencyRepository;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioRepository;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioDialog extends EntityDialog<Portfolio> {

	@Autowired
	ApplicationContext context;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	CurrencyRepository assetRepo;

	public PortfolioDialog(Portfolio en) {
		super(en, Portfolio.class);
	}

	@PostConstruct
	public void PostConstruct() {
		super.postConstruct(portRepo);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setupForm() {

		TextField nameField = new TextField("Name");
		nameField.setWidth("200px");
		binder.forField(nameField).asRequired().bind(Portfolio::getName, Portfolio::setName);

		ComboBox<Currency> ass = new ComboBox<>("Reserve Currency");
		ass.setItemLabelGenerator(c -> {
			return c.getName() +" ("+c.getKey()+")";
		});
		ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
		ass.setWidth("250px");
		binder.forField(ass).asRequired().bind(Portfolio::getReserve, Portfolio::setReserve);


		formLayout.startVerticalLayout();
		formLayout.add(nameField);
		formLayout.add(ass);
		formLayout.endLayout();


	}

}
