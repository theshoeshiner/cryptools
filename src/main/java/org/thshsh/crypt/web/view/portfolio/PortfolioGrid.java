package org.thshsh.crypt.web.view.portfolio;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.repository.Repository;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AppEasyRender;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.manage.ManagePortfolioView;
import org.thshsh.crypt.web.view.manage.PortfolioEntryGrid;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.data.StringSearchDataProvider;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.vaadin.addons.thshsh.easyrender.TemporalRenderer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class PortfolioGrid extends AppEntityGrid<Portfolio> {

	@Autowired
	TaskExecutor executor;

	@Autowired
	AllocationRepository allRepo;

	@Autowired
	PortfolioHistoryRepository histRepo;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	AppSession session;

	public PortfolioGrid() {
		super(PortfolioDialog.class, FilterMode.String);
		this.appendButtonColumn = true;
		this.showCount = SecurityUtils.hasAccess(Feature.Portfolio, Access.Super);
		this.showFilter = SecurityUtils.hasAccess(Feature.Portfolio, Access.Super);
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
	public DataProvider<Portfolio, ?> createRootDataProvider() {

		// Authentication authentication =
		// SecurityContextHolder.getContext().getAuthentication();
		// CryptUserPrincipal up = (CryptUserPrincipal) authentication.getPrincipal();
		// User user = session.getUser();

		StringSearchDataProvider<Portfolio> dp = new StringSearchDataProvider<>(portRepo, QuerySortOrder.asc("name").build());

		/*dp.setCountAllFunction(portRepo::countAllSecured);
		dp.setFindAllFunction(portRepo::findAllSecured);
		dp.setCountFilteredFunction(portRepo::countByStringSecured);
		dp.setFindFilteredFunction(portRepo::findByStringSecured);*/
		
		
		//dp.setSortOrders(QuerySortOrder.asc("name").build());

		/*CallbackDataProvider<Portfolio, Void> dataProvider = DataProvider.fromCallbacks(
				q -> portRepo.findByStringSecured(user,ChunkRequest.of(q, getDefaultSortOrder())).getContent().stream(),
				q -> Ints.checkedCast(portRepo.countByUser(user)));*/

		return dp;

		// return super.createDataProvider();
	}

	@Override
	public void setupColumns(Grid<Portfolio> grid) {

		// grid.addColumn(Portfolio::getName).setHeader("Name");
		

		grid.addColumn(
				AppEasyRender.router(ManagePortfolioView.class, Portfolio::getId, Portfolio::getName)
				)
				.setHeader("Name").setAutoWidth(true).setFlexGrow(0).setSortProperty("name");

		grid.addColumn(new NumberRenderer<>(
				BinderUtils.nestedValue(Portfolio::getLatest, PortfolioHistory::getMaxToTriggerPercent),
				PortfolioEntryGrid.PercentFormat)).setHeader("Alert").setWidth("100px").setFlexGrow(0)
				.setSortable(false);

		grid.addColumn(new NumberRenderer<>(BinderUtils.nestedValue(Portfolio::getLatest, PortfolioHistory::getValue),
				PortfolioEntryGrid.ReserveFormatWhole)).setHeader("Value").setWidth("100px").setSortable(false)
				.setFlexGrow(0);

		grid.addColumn(p -> {
			return String.join(", ", portRepo.findCurrencySymbols(p.getId()));
		}).setHeader("Currencies").setWidth("300px").setSortable(false).setFlexGrow(0);

		grid.addColumn(p -> {
			return String.join(", ", portRepo.findExchangeNames(p.getId()));
		}).setHeader("Exchanges").setWidth("300px").setSortable(false).setFlexGrow(0);

		if (SecurityUtils.hasAccess(Feature.User, Access.Read)) {
			grid.addColumn(BinderUtils.nestedValue(Portfolio::getUser, User::getEmail)).setHeader("User")
					.setSortProperty("user.email").setAutoWidth(true).setFlexGrow(0);
		}

		if (SecurityUtils.hasAccess(Feature.Portfolio, Access.Super)) {
			grid.addColumn(p -> {
				return histRepo.countByPortfolio(p);
			}).setHeader("History").setWidth("100px").setFlexGrow(0).setSortable(false);

			// TemporalRenderer<Source>

			grid
					// .addColumn( BinderUtils.nestedValue(Portfolio::getLatest,
					// PortfolioHistory::getTimestamp))
					.addColumn(new TemporalRenderer<>(
							BinderUtils.nestedValue(Portfolio::getLatest, PortfolioHistory::getTimestamp),
							DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)))
					.setHeader("Latest").setWidth("150px").setFlexGrow(0);
		}
	}

	/*@Override
	public void addButtonColumn(HorizontalLayout buttons, Portfolio e) {
		super.addButtonColumn(buttons, e);
	
			Button manageButton = new Button(VaadinIcon.FOLDER_OPEN.create());
			manageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(manageButton);
			UIUtils.setTitle(manageButton, "Manage");
			manageButton.addClickListener(click -> manage(e));
	}*/

	protected void manage(Portfolio e) {
		String route = RouteConfiguration.forSessionScope().getUrl(ManagePortfolioView.class);
		QueryParameters queryParameters = new QueryParameters(
				Collections.singletonMap(ManagePortfolioView.ID_PARAM, Arrays.asList(e.getId().toString())));
		UI.getCurrent().navigate(route, queryParameters);
	}

	/*@Override
	public void delete(Portfolio e) {
		portRepo.deleteById(e.getId());
		refresh();
	}*/

	/*@Override
	public void delete(Portfolio e, ConfirmDialog d, ButtonConfig bc) {
		ProgressBar pb = new ProgressBar();
		pb.setIndeterminate(true);
		HasOrderedComponents parent = (HasOrderedComponents) d.getButtonLayout().getParent().get();
		parent.addComponentAtIndex(parent.indexOf(d.getButtonLayout()), pb);
		bc.withClose(false);
		d.disableAllButtons();
		UI ui = UI.getCurrent();
		executor.execute(() -> {
			portRepo.deleteById(e.getId());
			ui.access(() -> {
				d.close();
				refresh();
			});
		});
		//super.delete(e, d, bc);
	
	}*/

	@Override
	public void setFilter(String text) {
		this.filterEntity.setName(text);
		this.filterEntity.setUser(new User(text, text, text));
	}

	@Override
	public void clearFilter() {
		this.filterEntity.setName(null);
		this.filterEntity.setUser(null);
	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Portfolio, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<Portfolio, Long> repository) {
		super.setRepository(repository);
	}

}
