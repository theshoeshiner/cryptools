package org.thshsh.crypt.web.view;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.cryptman.Portfolio;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioDialog extends org.thshsh.vaadin.entity.EntityDialog<Portfolio,Long> {

	/*
	 * @Autowired ApplicationContext context;
	 *
	 * @Autowired PortfolioRepository portRepo;
	 *
	 * @Autowired CurrencyRepository assetRepo;
	 */

	public PortfolioDialog(Portfolio en) {
		super(PortfolioForm.class,en);
	}

	/*
	 * @PostConstruct public void PostConstruct() { super.postConstruct(portRepo); }
	 *
	 * @SuppressWarnings("unchecked")
	 *
	 * @Override protected void setupForm() {
	 *
	 * TextField nameField = new TextField("Name"); nameField.setWidth("200px");
	 * binder.forField(nameField).asRequired().bind(Portfolio::getName,
	 * Portfolio::setName);
	 *
	 * ComboBox<Currency> ass = new ComboBox<>("Reserve Currency");
	 * ass.setItemLabelGenerator(c -> { return c.getName() +" ("+c.getKey()+")"; });
	 * ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
	 * ass.setWidth("250px");
	 * binder.forField(ass).asRequired().bind(Portfolio::getReserve,
	 * Portfolio::setReserve);
	 *
	 *
	 * formLayout.startVerticalLayout(); formLayout.add(nameField);
	 * formLayout.add(ass); formLayout.endLayout();
	 *
	 *
	 * }
	 */

}
