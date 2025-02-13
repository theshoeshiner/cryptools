package org.thshsh.crypt.web.view.manage;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.allocation.AllocationDescriptor;
import org.thshsh.crypt.web.view.allocation.AllocationDialog;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.data.ChunkRequest;

import com.google.common.primitives.Ints;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioAllocationGrid extends AppEntityGrid<Allocation> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioAllocationGrid.class);

	//@Autowired
	//ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;

	@Autowired
	AllocationRepository alloRepo;

	Portfolio portfolio;
	ManagePortfolioView view;
	Span remainder;

	public PortfolioAllocationGrid(ManagePortfolioView v) {
		super(AllocationDialog.class,FilterMode.String);
		//this.listOperationProvider = new PortfolioAllocationsListProvider();
		this.portfolio = v.getEntity();
		LOGGER.info("PortfolioBalancesList: {}", this.portfolio);
		this.showDeleteButton = true;
		this.appendButtonColumn = true;
		this.view = v;
		this.showCount=false;
		this.showFilter=false;
		this.showCreateButton=false;

	}

	@PostConstruct
	public void postConstruct() {
		
		super.setDescriptor(new AllocationDescriptor() {
			@Override
			public String getEntityName(Allocation t) {
				return PortfolioEntryGrid.PercentFormat.format(t.getPercent()) +" "+t.getCurrency().getKey();
			}
		});
		
		super.postConstruct();
		HorizontalLayout remainderLayout = new HorizontalLayout();
		remainderLayout.setPadding(false);
		remainderLayout.setMargin(false);
		this.addComponentAtIndex(this.indexOf(this.header) + 1, remainderLayout);
		remainder = new Span();
		remainderLayout.add(remainder);
		remainder.addClassName("count");
		updateRemainder();
	}

	//static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100l);

	protected void updateRemainder() {
		BigDecimal sum = alloRepo.findAllocationSumByPortfolio(portfolio).orElse(BigDecimal.ZERO);
		BigDecimal remainder = BigDecimal.ONE.subtract(sum);
		this.remainder.setText("Remainder: " + PortfolioEntryGrid.PercentFormat.format(remainder));
	}

	@Override
	public Dialog createDialog(Allocation entity) {
		LOGGER.info("createDialog: {}", this.portfolio);
		Dialog cd = (Dialog) appCtx.getBean(entityView, entity);
		return cd;
	}

	@Override
	public void refresh() {
		super.refresh();
		view.refreshValueRelatedTabs();
		updateRemainder();
	}

	/*public class PortfolioAllocationsListProvider extends DelegateEntitiesListProvider<Allocation,Long> {

		public PortfolioAllocationsListProvider() {
			this.list = PortfolioAllocationsList.this;
		}

		@Override
		public Long getEntityId(Allocation entity) {
			return entity.getId();
		}

		@Override
		public void setupColumns(Grid<Allocation> grid) {


			Column<?> curIcon = grid.addComponentColumn(entry -> {
				if(entry.getCurrency().getImageUrl()!=null) {
					String imageUrl = "/image/"+entry.getCurrency().getImageUrl();
					Image image = new Image(imageUrl,"Icon");
					image.setWidth(ManagePortfolioView.ICON_SIZE);
					image.setHeight(ManagePortfolioView.ICON_SIZE);
					return image;
				}
				else return new Span();

			})
			;
			UiComponents.iconColumn(curIcon);

			Column<?> lab = grid.addColumn(BinderUtils.nestedValue(Allocation::getCurrency, Currency::getName))
			.setHeader("Currency");

			UiComponents.iconLabelColumn(lab);

			grid.addColumn(new NumberRenderer<>(Allocation::getPercent,PortfolioEntriesList.PercentFormat))
			.setHeader("Percent");

			grid.addColumn(BinderUtils.nestedValue(Balance::getCurrency, Currency::getSymbol))
			.setHeader("Currency");

			grid.addColumn(Balance::getBalance)
			.setHeader("Balance");

		}

		@Override
		public void setFilter(String text) {

		}

		@Override
		public void clearFilter() {

		}

		@Override
		public QueryByExampleRepository<Allocation, Long> getRepository() {
			return alloRepo;
		}

		@Override
		public String getEntityName(Allocation t) {

			return PortfolioEntriesList.PercentFormat.format(t.getPercent()) +" "+t.getCurrency().getKey();
			//return t.getExchange().getName() +" / "+t.getBalance().toString();
		}



	}*/

	@Override
	public PagingAndSortingRepository<Allocation, Long> getRepository() {
		return alloRepo;
	}

	@Override
	public void setupColumns(Grid<Allocation> grid) {


		Column<?> curIcon = grid.addComponentColumn(entry -> {
			if(entry.getCurrency().getImageUrl()!=null) {
				String imageUrl = "/image/"+entry.getCurrency().getImageUrl();
				Image image = new Image(imageUrl,"Icon");
				image.setWidth(ManagePortfolioView.ICON_SIZE);
				image.setHeight(ManagePortfolioView.ICON_SIZE);
				return image;
			}
			else return new Span();

		})
		;
		UiComponents.iconColumn(curIcon);

		Column<?> lab = grid.addColumn(BinderUtils.nestedValue(Allocation::getCurrency, Currency::getName))
		.setHeader("Currency");

		UiComponents.iconLabelColumn(lab);

		grid.addColumn(new NumberRenderer<>(Allocation::getPercent,PortfolioEntryGrid.PercentFormat))
		.setHeader("Percent")
		.setFlexGrow(0)
		.setWidth("150px")
		;
		
		grid.addColumn(Allocation::getUndefined)
		.setHeader("Undefined")
		.setFlexGrow(0)
		.setWidth("150px")
		;

	}

	@Override
	public DataProvider<Allocation, ?> createRootDataProvider() {
	
		CallbackDataProvider<Allocation, Void> dataProvider = DataProvider.fromCallbacks(
				q -> alloRepo.findByPortfolio(portfolio,ChunkRequest.of(q, defaultSortOrders)).getContent().stream(),
				q -> Ints.checkedCast(alloRepo.countByPortfolio(portfolio)));

		return dataProvider;
	}

	

	/*@Override
	public Long getEntityId(Allocation entity) {
		return entity.getId();
	}
	*/
	@Override
	public void setFilter(String text) {

	}

	@Override
	public void clearFilter() {

	}

}