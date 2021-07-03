package org.thshsh.crypt.web.view;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.CryptUserPrincipal;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioRepository;
import org.thshsh.vaadin.ChunkRequest;
import org.thshsh.vaadin.UIUtils;
import org.thshsh.vaadin.entity.EntityGrid;

import com.google.common.primitives.Ints;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioGrid extends EntityGrid<Portfolio,Long> {

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	AppSession session;

	public PortfolioGrid() {
		super(Portfolio.class, PortfolioDialog.class, FilterMode.None);
		this.showButtonColumn=true;
	}



	@Override
	public DataProvider<Portfolio, ?> createDataProvider() {

		//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//CryptUserPrincipal up = (CryptUserPrincipal) authentication.getPrincipal();
		User user = session.getUser();

		CallbackDataProvider<Portfolio, Void> dataProvider = DataProvider.fromCallbacks(
				q -> portRepo.findByUser(user,ChunkRequest.of(q, getDefaultSortOrder())).getContent().stream(),
				q -> Ints.checkedCast(portRepo.countByUser(user)));

		return dataProvider;

		//return super.createDataProvider();
	}



	@Override
	public PagingAndSortingRepository<Portfolio, Long> getRepository() {
		return portRepo;
	}

	@Override
	public void setupColumns(Grid<Portfolio> grid) {
		grid.addColumn(Portfolio::getName).setHeader("Name");

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
		QueryParameters queryParameters = new QueryParameters(Collections.singletonMap(ManagePortfolioView.ID_PARAM,Arrays.asList(e.getId().toString())));
		UI.getCurrent().navigate(route, queryParameters);
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
