package org.thshsh.crypt.web.view;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.ChunkRequest;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.RouterLinkRenderer;
import org.thshsh.vaadin.StringSearchDataProvider;
import org.thshsh.vaadin.UIUtils;

import com.google.common.primitives.Ints;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioGrid extends AppEntityGrid<Portfolio,Long> {

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	AppSession session;

	public PortfolioGrid() {
		super(Portfolio.class, PortfolioDialog.class, FilterMode.String);
		this.showButtonColumn=true;
		this.showCount=false;
		this.showFilter=false;
		this.showEditButton = SecurityUtils.hasAccess(Portfolio.class, Access.ReadWrite);
		this.showDeleteButton = SecurityUtils.hasAccess(Portfolio.class, Access.ReadWriteDelete);
	}

	@Override
	 @PostConstruct
	public void postConstruct() {
		super.postConstruct();
		buttonColumn.setFlexGrow(1);
	}


	@Override
	public DataProvider<Portfolio, ?> createDataProvider() {

		//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//CryptUserPrincipal up = (CryptUserPrincipal) authentication.getPrincipal();
		//User user = session.getUser();
		
		StringSearchDataProvider<Portfolio, Long> dp = new StringSearchDataProvider<>(portRepo,getDefaultSortOrder());

		dp.setCountAllFunction(portRepo::countAllSecured);
		dp.setFindAllFunction(portRepo::findAllSecured);
		dp.setCountFilteredFunction(portRepo::countByStringSecured);
		dp.setFindFilteredFunction(portRepo::findByStringSecured);
		dp.setSortOrders(QuerySortOrder.asc("name").build());
		
		/*CallbackDataProvider<Portfolio, Void> dataProvider = DataProvider.fromCallbacks(
				q -> portRepo.findByStringSecured(user,ChunkRequest.of(q, getDefaultSortOrder())).getContent().stream(),
				q -> Ints.checkedCast(portRepo.countByUser(user)));*/

		return dp;

		//return super.createDataProvider();
	}



	@Override
	public PagingAndSortingRepository<Portfolio, Long> getRepository() {
		return portRepo;
	}

	@Override
	public void setupColumns(Grid<Portfolio> grid) {
		
		//grid.addColumn(Portfolio::getName).setHeader("Name");

		grid
		.addColumn(new RouterLinkRenderer<>(ManagePortfolioView.class, Portfolio::getName, Portfolio::getId))
		.setHeader("Name")
		.setAutoWidth(true)
		.setFlexGrow(0)
		;
		
		if(SecurityUtils.hasAccess(Feature.User, Access.Read)) {
			grid.addColumn(FunctionUtils.nestedValue(Portfolio::getUser, User::getUserName))
			.setHeader("User")
			.setSortProperty("user.userName")
			.setAutoWidth(true)
			.setFlexGrow(0)
			;
		}
		
		/*grid.getHeaderRows().forEach(hr -> {
			
		});*/
	}
	
	@Override
	public void addButtonColumn(HorizontalLayout buttons, Portfolio e) {
		super.addButtonColumn(buttons, e);

		/*	Button manageButton = new Button(VaadinIcon.FOLDER_OPEN.create());
			manageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(manageButton);
			UIUtils.setTitle(manageButton, "Manage");
			manageButton.addClickListener(click -> manage(e));*/
	}

	protected void manage(Portfolio e) {
		String route = RouteConfiguration.forSessionScope().getUrl(ManagePortfolioView.class);
		QueryParameters queryParameters = new QueryParameters(Collections.singletonMap(ManagePortfolioView.ID_PARAM,Arrays.asList(e.getId().toString())));
		UI.getCurrent().navigate(route, queryParameters);
	}
	
	

	@Override
	public void delete(Portfolio e) {
		portRepo.deleteById(e.getId());
		refresh();
	}



	@Override
	public String getEntityName(Portfolio t) {
		return t.getName();
	}

	@Override
	public Long getEntityId(Portfolio entity) {
		return entity.getId();
	}

	@Override
	public void setFilter(String text) {

	}

	@Override
	public void clearFilter() {

	}

}
