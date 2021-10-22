package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.PortfolioSummary;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.serv.MarketRateService;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.GridUtils;
import org.thshsh.vaadin.ImageRenderer;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.NumberRenderer;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioEntryGrid extends AppEntityGrid<PortfolioEntryHistory, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioBalanceGrid.class);

	public static NumberFormat ReserveFormatWhole = new DecimalFormat("$#,##0");
	public static NumberFormat ReserveFormat = new DecimalFormat("$#,##0.00");
	public static NumberFormat PercentFormat = new DecimalFormat("##.#%");
	public static NumberFormat CoinFormat = new DecimalFormat("####.########");

	//@Autowired
	//ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;
	
	@Autowired
	ImageService imageService;

	@Autowired
	AllocationRepository alloRepo;

	//@Autowired
	//MarketRateService rateService;

	@Autowired
	ManagePortfolioService portService;

	Portfolio portfolio;
	ManagePortfolioView view;

	Set<PortfolioEntryHistory> entries;

	ListDataProvider<PortfolioEntryHistory> dataProvider;

	FooterCell totalValueCell;
	FooterCell totalAdjustCell;

	BigDecimal indThreshold = new BigDecimal(".15");
	BigDecimal portThreshold = new BigDecimal(".04");
	FooterRow footer;
	PortfolioHistory summary;

	public PortfolioEntryGrid(ManagePortfolioView v) {
		super(PortfolioEntryHistory.class, null, FilterMode.None);
		//this.listOperationProvider = new PortfolioEntriesListProvider();
		this.portfolio = v.entity;
		LOGGER.info("PortfolioBalancesList: {}", this.portfolio);
		this.showDeleteButton = false;
		this.showEditButton=false;
		this.showButtonColumn = false;
		this.view = v;
		//this.entries = new HashSet<PortfolioEntryHistory>();
		this.showHeader = true;
	}

	@PostConstruct
	public void postConstruct() {

		summary = portService.createHistory(portfolio);
		entries = summary.getEntries();
		BigDecimal tot = summary.getValue();
		dataProvider = new ListDataProvider<>(entries);
		super.postConstruct();

		totalValueCell.setText(ReserveFormat.format(tot));
		
		advancedButton.setVisible(true);
		count.setVisible(false);
		filter.setVisible(false);
		
		Button addBalance = new Button("Add Balance");
		countAndAdvanced.add(addBalance);
		

	}

	@Override
	public Dialog createDialog(PortfolioEntryHistory entity) {
		LOGGER.info("createDialog: {}", this.portfolio);
		Dialog cd = (Dialog) appCtx.getBean(entityView, entity, portfolio);
		return cd;
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	public Long getCountAll() {
		return (long) dataProvider.size(new Query<>());
	}

	static BigDecimal[] warn = new BigDecimal[] { new BigDecimal("1"), new BigDecimal(".75"), new BigDecimal(".5"),
			new BigDecimal(".25") };


	@Override
	public PagingAndSortingRepository<PortfolioEntryHistory, Long> getRepository() {
		return null;
	}

	@Override
	public Long getEntityId(PortfolioEntryHistory entity) {
		return entity.getCurrency().getId();
	}

	@Override
	public String getEntityName(PortfolioEntryHistory t) {
		return "";
	}


	protected void adjustAllocation(PortfolioEntryHistory pe) {

		Allocation allo = pe.getAllocation();
		LOGGER.info("current allocation: {}",allo);
		AllocationDialog dialog;
		if(allo == null || allo.getId() == null) {
			//using the default allocation
			dialog = this.appCtx.getBean(AllocationDialog.class,null,pe.getPortfolio(),pe.getCurrency());
		}
		else {
			allo = alloRepo.findById(allo.getId()).get();
			dialog = this.appCtx.getBean(AllocationDialog.class,allo,allo.getPortfolio(),allo.getCurrency());
		}
		dialog.addOpenedChangeListener(changed -> {
			if(dialog.getEntityForm().getSaved()) {
				this.view.refreshMainTab();
				view.runHistoryJob();
			}
		});

		dialog.open();
	}

	@Override
	public void setupColumns(Grid<PortfolioEntryHistory> grid) {


		
		Column<?> curCol = grid.addColumn(new ImageRenderer<>(entry -> {
			return imageService.getImageUrl(entry.getCurrency());
		}, null, ManagePortfolioView.ICON_SIZE, ManagePortfolioView.ICON_SIZE));

		UiComponents.iconColumn(curCol);

		Column<?> sym = grid.addColumn(FunctionUtils.nestedValue(PortfolioEntryHistory::getCurrency, Currency::getKey))
				.setHeader("Currency")
				.setWidth("90px")
				.setFlexGrow(0)
				.setSortProperty("currency.symbol");
		UiComponents.iconLabelColumn(sym);



		grid.addComponentColumn(entry -> {
			Span node = new Span();
			String perc = PercentFormat.format(entry.getAllocation().getPercent());
			if(entry.getAllocation().isUndefined()) {
				Span auto = new Span("(auto)");
				auto.addClassName("auto");
				node.add(auto);
			}
			node.add(new Span(perc));

			return node;
		})
		.setHeader("Allocation")
		//.setSortProperty("allocation.percent")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator
				.comparing(FunctionUtils.nestedValue(PortfolioEntryHistory::getAllocation, Allocation::getPercent)))
		.setWidth("100px")
		.setFlexGrow(0);
		;

		
		grid.addComponentColumn(e -> {

			HorizontalLayout buttons = new HorizontalLayout();
			buttons.addClassName("grid-buttons");
			buttons.setPadding(true);
			buttons.setWidthFull();
			buttons.setJustifyContentMode(JustifyContentMode.END);

			Button manageButton = new Button(VaadinIcon.PENCIL.create());
			manageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(manageButton);
			UIUtils.setTitle(manageButton, "Allocation");
			manageButton.addClickListener(click -> adjustAllocation(e));
			manageButton.addClassName("link");
			buttons.add(manageButton);


			return buttons;
		})
		.setFlexGrow(0)
		.setClassNameGenerator(val -> {
			return "grid-buttons-column";
		})
		.setWidth("40px");
		

		grid.addColumn(new NumberRenderer<>(FunctionUtils.nestedValue(PortfolioEntryHistory::getRate, MarketRate::getRate),ReserveFormat))
			.setHeader("Price")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(FunctionUtils.nestedValue(PortfolioEntryHistory::getRate, MarketRate::getRate)))
			.setWidth("100px")
			.setFlexGrow(0);

		grid.addColumn(PortfolioEntryHistory::getBalance)
			.setSortProperty("balance")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getBalance))
			.setHeader("Balance")
			.setWidth("125px")
			.setFlexGrow(0);

		Column<?> valueColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getValueReserve, ReserveFormat))
			.setHeader("Value")
			.setTextAlign(ColumnTextAlign.END)
			.setSortProperty("value")
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getValueReserve))
			.setWidth("110px")
			.setFlexGrow(0);

		

		grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getAdjustReserve, ReserveFormat))
			.setHeader("Adjust $")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getAdjustAbsolute))
			.setWidth("125px")
			.setFlexGrow(0);

		//CoinFormat



		//grid.addColumn(PortfolioEntry::getAdjust)
		grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getAdjust, CoinFormat))
			.setHeader("Adjust ©")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getAdjust))
			//.setWidth("100px")
			//.setFlexGrow(0)
			.setWidth("110px")
			.setFlexGrow(0);

		

	

		grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getToTriggerPercent, PercentFormat))
			.setHeader("Alert %")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getToTriggerPercentOrZero))
			.setWidth("110px")
			.setFlexGrow(0)
		;

		grid.addColumn(e -> {
			return "";
		});

		grid.setClassNameGenerator(pe -> {
			List<String> names = new ArrayList<>();
			/*if(pe.getToTriggerPercentOrZero().compareTo(BigDecimal.ONE) > 0) {
				LOGGER.info("alert on: {}",pe);
				names.add("alert");
			}*/
			/*else if(pe.getToTriggerPercentOrZero().compareTo(BigDecimal.ONE) > 0) {
				LOGGER.info("alert on: {}",pe);
				return "alert";
			}*/

			Integer warnLevel = getWarnLevel(pe.getToTriggerPercent());
			names.add("warn-"+warnLevel);
			
			/*for (int i = 0; i < warn.length; i++) {
				BigDecimal b = warn[i];
				if (pe.getToTriggerPercentOrZero().compareTo(b) > 0) {
					names.add("warn-" + i);
					break;
				}
			}*/

			return StringUtils.join(names, " ");
			//return new StringJoiner(" ",)
		});

		footer = grid.appendFooterRow();
		totalValueCell = footer.getCell(valueColumn);
		

	}
	
	public static Integer getWarnLevel(BigDecimal bd) {
		if(bd != null) {
			for (int i = 0; i < warn.length; i++) {
				if (bd.compareTo(warn[i]) > 0) {
					return i;
				}
			}
		}
		return null;
	}

	
	@Override
	public void setupAdvancedColumns(Grid<PortfolioEntryHistory> grid, Collection<Column<PortfolioEntryHistory>> coll) {
		
		Column<PortfolioEntryHistory> c = grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getTargetReserve, ReserveFormat))
				.setHeader("Target")
				.setTextAlign(ColumnTextAlign.END)
				.setComparator(Comparator.comparing(PortfolioEntryHistory::getTargetReserve))
				.setWidth("110px")
				.setFlexGrow(0);
		
		coll.add(c);

		Column<PortfolioEntryHistory> adjustColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getAdjustPercent, PercentFormat))
				.setHeader("Adjust %")
				.setTextAlign(ColumnTextAlign.END)
				.setComparator(Comparator.comparing(PortfolioEntryHistory::getAdjustPercentAbsolute))
				.setWidth("100px")
				.setFlexGrow(0);
		
		coll.add(adjustColumn);
		
		GridUtils.reorderColumns(grid, c,6,adjustColumn,8);
		
		totalAdjustCell = footer.getCell(adjustColumn);
		totalAdjustCell.setText(PercentFormat.format(summary.getTotalAdjustPercent()));
	}

	@Override
	public void setFilter(String text) {

	}

	@Override
	public void clearFilter() {

	}

	@Override
	public DataProvider<PortfolioEntryHistory, ?> createDataProvider() {
		ListDataProvider<PortfolioEntryHistory> ldp = new ListDataProvider<>(entries);
		ldp.setSortOrder(PortfolioEntryHistory::getToTriggerPercentOrZero, SortDirection.DESCENDING);
		return ldp;

	}

}
