package org.thshsh.crypt.web.view;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioRepository;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;

@SuppressWarnings("serial")
@Route(value = "portfolios", layout = MainLayout.class)
@PageTitle("Portfolios")
public class PortfoliosView extends EntitiesView<Portfolio, Long> {

	@Autowired
	PortfolioRepository portRepo;

	public PortfoliosView() {
		super(Portfolio.class, PortfolioDialog.class);
	}

	@Override
	public ExampleFilterRepository<Portfolio, Long> getRepository() {
		return portRepo;
	}

	@Override
	public String getEntityName(Portfolio t) {
		return t.getName();
	}

	@Override
	public void setupColumns(Grid<Portfolio> grid) {
		grid.addColumn(Portfolio::getName).setHeader("Name");

	}



	@Override
	public void postConstruct() {
		this.entitiesList.showButtonColumn=true;
		super.postConstruct();
	}

	@Override
	public void addButtonColumn(HorizontalLayout buttons, Portfolio e) {
		super.addButtonColumn(buttons, e);

		Button manageButton = new Button(VaadinIcon.FOLDER_OPEN.create());
		manageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		buttons.add(manageButton);
		UIUtils.setTitle(manageButton, "Manage");
		manageButton.addClickListener(click -> manage(e));
	}

	protected void manage(Portfolio e) {
		String route = RouteConfiguration.forSessionScope().getUrl(ManagePortfolioView.class);
		QueryParameters queryParameters = new QueryParameters(Collections.singletonMap("id",Arrays.asList(e.getId().toString())));
		UI.getCurrent().navigate(route, queryParameters);
	}

	@Override
	public void setFilter(String text) {


	}

	@Override
	public void clearFilter() {

	}



}
