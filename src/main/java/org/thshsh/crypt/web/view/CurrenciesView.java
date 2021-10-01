package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.thshsh.crypt.Currency;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@SuppressWarnings("serial")
@Route(value = "currencies", layout = MainLayout.class)
@PageTitle("Currencies")
public class CurrenciesView extends EntityGridView<Currency, Long> {


	public CurrenciesView() {
		super(CurrenciesGrid.class);
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
	}


}
