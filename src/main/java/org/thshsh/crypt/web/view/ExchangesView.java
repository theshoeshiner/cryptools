package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.ExchangeRepository;
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

	//@Autowired
	//ExchangeRepository exchangeRepo;

	public ExchangesView() {
		super(ExchangesGrid.class);
	}

	@PostConstruct
	public void postConstruct() {

		super.postConstruct();
		//this.entitiesList.showButtonColumn = true;
	}

	/*@Override
	public PagingAndSortingRepository<Exchange, Long> getRepository() {
		return exchangeRepo;
	}

	@Override
	public String getEntityName(Exchange t) {
		return t.getName();
	}

	@Override
	public void setupColumns(Grid<Exchange> grid) {

		grid.addComponentColumn(e -> {
			if(e.getImageUrl()!=null) {
				String imageUrl = "/image/"+e.getImageUrl();
				Image image = new Image(imageUrl,"Icon");
				image.setWidth(ManagePortfolioView.ICON_SIZE);
				image.setHeight(ManagePortfolioView.ICON_SIZE);
				return image;
			}
			else return new Span();

		})
		.setWidth("48px")
		.setFlexGrow(0)
		.setSortable(false)
		.setClassNameGenerator(pe -> {
			return "icon";
		})
		;

		 grid.addColumn(Exchange::getName)
		 .setHeader("Name")
		 .setSortProperty("name")
		 ;

		 grid.addColumn(Exchange::getKey)
		 .setHeader("Key")
		 .setSortProperty("key")
		 ;

	}

	@Override
	public void setFilter(String text) {

	}

	@Override
	public void clearFilter() {

	}*/



}
