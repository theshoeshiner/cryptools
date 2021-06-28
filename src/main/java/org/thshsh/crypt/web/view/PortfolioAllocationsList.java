package org.thshsh.crypt.web.view;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.cryptman.Allocation;
import org.thshsh.cryptman.AllocationRepository;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.FunctionUtils;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;



@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioAllocationsList extends EntitiesList<Allocation, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioAllocationsList.class);

	@Autowired
	ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;

	@Autowired
	AllocationRepository alloRepo;

	Portfolio portfolio;
	ManagePortfolioView view;
	Span remainder;

	public PortfolioAllocationsList(ManagePortfolioView v) {
		super(Allocation.class, AllocationDialog.class);
		this.listOperationProvider = new PortfolioAllocationsListProvider();
		this.portfolio = v.entity;
		LOGGER.info("PortfolioBalancesList: {}",this.portfolio);
		this.showDeleteButton = true;
		this.showButtonColumn = true;
		this.view = v;

	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct(appContext);
		HorizontalLayout remainderLayout = new HorizontalLayout();
		remainderLayout.setPadding(false);
		remainderLayout.setMargin(false);
		this.addComponentAtIndex(this.indexOf(this.header)+1, remainderLayout);
		remainder = new Span();
		remainderLayout.add(remainder);
		remainder.addClassName("count");
		updateRemainder();
	}

	//static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100l);

	protected void updateRemainder() {
		BigDecimal sum = alloRepo.findAllocationSumByPortfolio(portfolio);
		BigDecimal remainder = BigDecimal.ONE.subtract(sum);
		this.remainder.setText("Remainder: "+PortfolioEntriesList.PercentFormat.format(remainder));
	}

	@Override
	public Dialog createDialog(Allocation entity) {
		LOGGER.info("createDialog: {}",this.portfolio);
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity,portfolio);
		return cd;
	}



	@Override
	public void refresh() {
		super.refresh();
		view.refreshMainTab();
	}



	public class PortfolioAllocationsListProvider extends DelegateEntitiesListProvider<Allocation,Long> {

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

			Column<?> lab = grid.addColumn(FunctionUtils.nestedValue(Allocation::getCurrency, Currency::getName))
			.setHeader("Currency");

			UiComponents.iconLabelColumn(lab);

			grid.addColumn(new NumberRenderer<>(Allocation::getPercent,PortfolioEntriesList.PercentFormat))
			.setHeader("Percent");

			/*grid.addColumn(FunctionUtils.nestedValue(Balance::getCurrency, Currency::getSymbol))
			.setHeader("Currency");

			grid.addColumn(Balance::getBalance)
			.setHeader("Balance");*/

		}

		@Override
		public void setFilter(String text) {

		}

		@Override
		public void clearFilter() {

		}

		@Override
		public ExampleFilterRepository<Allocation, Long> getRepository() {
			return alloRepo;
		}

		@Override
		public String getEntityName(Allocation t) {

			return PortfolioEntriesList.PercentFormat.format(t.getPercent()) +" "+t.getCurrency().getKey();
			//return t.getExchange().getName() +" / "+t.getBalance().toString();
		}



	}

}