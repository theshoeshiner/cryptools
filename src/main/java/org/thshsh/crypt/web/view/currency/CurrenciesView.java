package org.thshsh.crypt.web.view.currency;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.web.view.Breadcrumbs;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@SuppressWarnings("serial")
@Route(value = "currencies", layout = MainLayout.class)
@PageTitle("Currencies")
public class CurrenciesView extends EntityGridView<Currency, Long> {

	@Autowired
	Breadcrumbs breadcrumbs;

	public CurrenciesView() {
		super(CurrenciesGrid.class);
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		 breadcrumbs.resetBreadcrumbs()
		    .addBreadcrumb("Currencies", CurrenciesView.class)
		    ;
	}


}
