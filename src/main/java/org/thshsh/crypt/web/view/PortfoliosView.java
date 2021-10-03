package org.thshsh.crypt.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "portfolios", layout = MainLayout.class)
@PageTitle(PortfoliosView.TITLE)
public class PortfoliosView extends EntityGridView<Portfolio, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfoliosView.class);

	public static final String TITLE = "Portfolios";

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	Breadcrumbs breadcrumbs;
	
	public PortfoliosView() {
		super(PortfolioGrid.class);
	}

	/*@Override
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

	}*/



	@Override
	public void postConstruct() {
		LOGGER.info("post construct");
		//this.entitiesList.showButtonColumn=true;
		super.postConstruct();
		
		 breadcrumbs.resetBreadcrumbs()
		    .addBreadcrumb(PortfoliosView.TITLE, PortfoliosView.class)
		    ;
	}

	/*@Override
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

	}*/



}
