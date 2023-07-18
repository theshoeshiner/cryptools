package org.thshsh.crypt.web.view.portfolio;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.crypt.web.view.balance.BalanceDialog;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.data.ChunkRequest;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.google.common.primitives.Ints;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioBalanceGrid extends AppEntityGrid<Balance> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioBalanceGrid.class);

	//@Autowired
	//ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;
	
	@Autowired
	AllocationRepository alloRepo;
	
	@Autowired
	ManagePortfolioService manageService;
	
	Portfolio portfolio;
	ManagePortfolioView manageView;
	
	public PortfolioBalanceGrid(ManagePortfolioView v) {
		super(BalanceDialog.class,FilterMode.None);

		this.portfolio = v.getEntity();
		LOGGER.info("PortfolioBalancesList: {}",this.portfolio);
		this.showDeleteButton = true;
		this.appendButtonColumn = true;
		this.manageView = v;
		this.showCount = false;
		this.showFilter=false;
		this.createText="Add";

	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		//this.header.setMargin(false);
		this.setPadding(false);
		this.setSpacing(false);
		this.setMargin(false);
		
	}

	@Override
	public Dialog createDialog(Balance entity) {
		LOGGER.info("createDialog: {}",this.portfolio);
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity,portfolio);
		return cd;
	}



	@Override
	public DataProvider<Balance, ?> createDataProvider() {
		CallbackDataProvider<Balance, Void> dataProvider = DataProvider.fromCallbacks(
				q -> balanceRepo.findByPortfolio(portfolio,ChunkRequest.of(q, getDefaultSortOrder())).getContent().stream(),
				q -> Ints.checkedCast(balanceRepo.countByPortfolio(portfolio)));

		return dataProvider;
	}

	@Override
	public void refresh() {
		//This gets called when something is deleted or balance dialog is saved
		super.refresh();
		//create new history
		manageService.createHistory(portfolio);
		manageView.refreshSummaryTab(true);
		manageView.refreshDistributionChart();
		manageView.refreshValueChart();
		manageView.refreshAllocationTab();
	}




	@Override
	public void clickNew(ClickEvent<Button> click) {
		super.clickNew(click);
	}

	@Override
	public void delete(Collection<Balance> entities) {
		
		for(Balance e : entities) {

			Collection<Balance> balances = balanceRepo.findByPortfolioAndCurrency(portfolio, e.getCurrency());
			if(balances.size() == 1) {
				//TODO if this is the last balance then we should delete the allocation as well
				alloRepo.findByPortfolioAndCurrency(portfolio, e.getCurrency()).ifPresent(a -> {
					LOGGER.info("deleting allocation: {}",a);
					alloRepo.delete(a);
					//if we're deleting an allocation then we need to make sure we're not leaving a remainder?
					//no we dont want to change other allocations without explicitly telling the user
				});
				((BalanceRepository)repository).delete(e);
	
			}
		
		}
		//super.delete(entities);
	}

	


	@Override
	public void setupColumns(Grid<Balance> grid) {

		Column<?> col = grid.addComponentColumn(entry -> {
			if(entry.getExchange() != null && entry.getExchange().getImageUrl()!=null) {
				String imageUrl = "/image/"+entry.getExchange().getImageUrl();
				Image image = new Image(imageUrl,"Icon");
				image.setWidth(ManagePortfolioView.ICON_SIZE);
				image.setHeight(ManagePortfolioView.ICON_SIZE);
				return image;
			}
			else return new Span();

		});
		UiComponents.iconColumn(col);

		Column<?> eName = grid.addColumn(BinderUtils.nestedValue(Balance::getExchange, Exchange::getName))
		.setHeader("Exchange")
		.setSortProperty("exchange.name")
		.setSortable(true)
		.setWidth("125px")
		.setFlexGrow(0)
		;
		UiComponents.iconLabelColumn(eName);

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

		Column<?> label = grid.addColumn(BinderUtils.nestedValue(Balance::getCurrency, Currency::getKey))
		.setHeader("Currency")
		.setSortProperty("currency.key")
		.setSortable(true)
		.setWidth("125px")
		.setFlexGrow(0)
		;
		UiComponents.iconLabelColumn(label);

		grid.addColumn(Balance::getBalance)
		.setHeader("Balance")
		.setSortProperty("balance")
		.setSortable(true)
		;

	}


	@Override
	public void setFilter(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearFilter() {
		// TODO Auto-generated method stub

	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<Balance, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(Repository<Balance, Long> repository) {
		super.setRepository(repository);
	}
	
	

	/*
	public static class PortfolioBalancesListProvider extends DelegateEntitiesListProvider<Balance,Long> {

		public PortfolioBalancesListProvider() {
			this.list = PortfolioBalancesList.this;
		}

		@Override
		public Long getEntityId(Balance entity) {
			return entity.getId();
		}

		@Override
		public void setupColumns(Grid<Balance> grid) {

			Column<?> col = grid.addComponentColumn(entry -> {
				if(entry.getExchange().getImageUrl()!=null) {
					String imageUrl = "/image/"+entry.getExchange().getImageUrl();
					Image image = new Image(imageUrl,"Icon");
					image.setWidth(ManagePortfolioView.ICON_SIZE);
					image.setHeight(ManagePortfolioView.ICON_SIZE);
					return image;
				}
				else return new Span();

			});
			UiComponents.iconColumn(col);

			Column<?> eName = grid.addColumn(BinderUtils.nestedValue(Balance::getExchange, Exchange::getName))
			.setHeader("Exchange")
			.setSortProperty("exchange.name")
			.setSortable(true)
			.setWidth("150px")
			.setFlexGrow(0)
			;
			UiComponents.iconLabelColumn(eName);

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

			Column<?> label = grid.addColumn(BinderUtils.nestedValue(Balance::getCurrency, Currency::getKey))
			.setHeader("Currency")
			.setSortProperty("currency.key")
			.setSortable(true)
			.setWidth("125px")
			.setFlexGrow(0)
			;
			UiComponents.iconLabelColumn(label);

			grid.addColumn(Balance::getBalance)
			.setHeader("Balance")
			.setSortProperty("balance")
			.setSortable(true)
			;

		}

		@Override
		public void setFilter(String text) {

		}

		@Override
		public void clearFilter() {

		}

		@Override
		public QueryByExampleRepository<Balance, Long> getRepository() {
			return balanceRepo;
		}

		@Override
		public String getEntityName(Balance t) {
			return t.getBalance().toString() +" "+t.getCurrency().getKey();
			//return t.getExchange().getName() +" / "+t.getBalance().toString();
		}



	}*/


}
