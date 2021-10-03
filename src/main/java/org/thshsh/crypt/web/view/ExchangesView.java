package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@SuppressWarnings("serial")
@Route(value = "exchanges", layout = MainLayout.class)
@PageTitle("Exchanges")
public class ExchangesView extends EntityGridView<Exchange, Long> {


	public ExchangesView() {
		super(ExchangesGrid.class);
	}

	@PostConstruct
	public void postConstruct() {

		super.postConstruct();
		//this.entitiesList.showButtonColumn = true;
	}




}
