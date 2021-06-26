package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.cryptman.Balance;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.FunctionUtils;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioBalancesList extends EntitiesList<Balance, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioBalancesList.class);

	@Autowired
	ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;

	Portfolio portfolio;
	ManagePortfolioView view;

	public PortfolioBalancesList(ManagePortfolioView v) {
		super(Balance.class, BalanceDialog.class);
		this.listOperationProvider = new PortfolioBalancesListProvider();
		this.portfolio = v.entity;
		LOGGER.info("PortfolioBalancesList: {}",this.portfolio);
		this.showDeleteButton = true;
		this.showButtonColumn = true;
		this.view = v;

	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct(appContext);
	}

	@Override
	public Dialog createDialog(Balance entity) {
		LOGGER.info("createDialog: {}",this.portfolio);
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity,portfolio);
		return cd;
	}



	@Override
	public void refresh() {
		super.refresh();
		view.refreshMainTab();
	}



	public class PortfolioBalancesListProvider extends DelegateEntitiesListProvider<Balance,Long> {

		public PortfolioBalancesListProvider() {
			this.list = PortfolioBalancesList.this;
		}

		@Override
		public Long getEntityId(Balance entity) {
			return entity.getId();
		}

		@Override
		public void setupColumns(Grid<Balance> grid) {

			grid.addComponentColumn(entry -> {
				if(entry.getExchange().getImageUrl()!=null) {
					String imageUrl = "/image/"+entry.getExchange().getImageUrl();
					Image image = new Image(imageUrl,"Icon");
					image.setWidth(ManagePortfolioView.ICON_SIZE);
					image.setHeight(ManagePortfolioView.ICON_SIZE);
					return image;
				}
				else return new Span();

			})
			.setWidth("48px")
			.setFlexGrow(0)
			//.setSortProperty("exchange.name")
			.setClassNameGenerator(pe -> {
				return "icon";
			})

			;

			grid.addColumn(FunctionUtils.nestedValue(Balance::getExchange, Exchange::getName))
			.setHeader("Exchange")
			.setSortProperty("exchange.name")
			.setSortable(true)
			.setWidth("150px")
			.setFlexGrow(0)
			;

			grid.addComponentColumn(entry -> {
				if(entry.getCurrency().getImageUrl()!=null) {
					String imageUrl = "/image/"+entry.getCurrency().getImageUrl();
					Image image = new Image(imageUrl,"Icon");
					image.setWidth(ManagePortfolioView.ICON_SIZE);
					image.setHeight(ManagePortfolioView.ICON_SIZE);
					return image;
				}
				else return new Span();

			})
			.setWidth("48px")
			.setFlexGrow(0)
			//.setSortProperty("currency.key")
			.setClassNameGenerator(pe -> {
				return "icon";
			})
			;

			grid.addColumn(FunctionUtils.nestedValue(Balance::getCurrency, Currency::getKey))
			.setHeader("Currency")
			.setSortProperty("currency.key")
			.setSortable(true)
			.setWidth("125px")
			.setFlexGrow(0)
			;

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
		public ExampleFilterRepository<Balance, Long> getRepository() {
			return balanceRepo;
		}

		@Override
		public String getEntityName(Balance t) {
			return t.getBalance().toString() +" "+t.getCurrency().getKey();
			//return t.getExchange().getName() +" / "+t.getBalance().toString();
		}



	}

}
