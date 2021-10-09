package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@SuppressWarnings("serial")
@Route(value = "exchanges", layout = MainLayout.class)
@PageTitle("Exchanges")
public class ExchangesView extends EntityGridView<Exchange, Long> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ExchangesView.class);

	@Autowired
	Breadcrumbs breadcrumbs;
	
	public ExchangesView() {
		super(ExchangesGrid.class);
	}

	@PostConstruct
	public void postConstruct() {

		super.postConstruct();
		//this.entitiesList.showButtonColumn = true;
		
		LOGGER.info("breadcrumbs: {}",breadcrumbs);
		
		 breadcrumbs.resetBreadcrumbs()
		    .addBreadcrumb("Exchanges", ExchangesView.class)
		    ;
	}




}
