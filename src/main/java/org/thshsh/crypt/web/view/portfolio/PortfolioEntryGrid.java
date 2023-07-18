package org.thshsh.crypt.web.view.portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Allocation;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.AllocationRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.serv.BalanceService;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.web.UiComponents;
import org.thshsh.crypt.web.view.AppEntityGrid;
import org.thshsh.crypt.web.view.ManagePortfolioView;
import org.thshsh.crypt.web.view.allocation.AllocationDialog;
import org.thshsh.vaadin.BinderUtils;
import org.thshsh.vaadin.GridUtils;
import org.thshsh.vaadin.UIUtils;
import org.thshsh.vaadin.entity.ConfirmDialogs;
import org.thshsh.vaadin.entity.EntityDescriptor;
import org.thshsh.vaadin.entity.EntityGrid;
import org.vaadin.addons.thshsh.easyrender.ImageRenderer;
import org.vaadin.addons.thshsh.hovercolumn.HoverColumn;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
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
public class PortfolioEntryGrid extends AppEntityGrid<PortfolioEntryHistory> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioEntryGrid.class);

	public static NumberFormat ReserveFormatWhole = new DecimalFormat("$#,##0");
	public static NumberFormat ReserveFormat = new DecimalFormat("$#,##0.00");
	public static NumberFormat ReserveFormatFull = new DecimalFormat("$#,##0.00#####");
	public static NumberFormat PercentFormat = new DecimalFormat("##.#%");
	public static NumberFormat CoinFormat = new DecimalFormat("####.########");

	static BigDecimal[] WARN = new BigDecimal[] { 
			new BigDecimal("1"), 
			new BigDecimal(".85"), 
			new BigDecimal(".75"),
			new BigDecimal(".5") ,
			new BigDecimal("0") 		
	};
	
	static Integer MAX_SCALE = 6;

	@Autowired
	BalanceService balanceServ;
	
	@Autowired
	BalanceRepository balanceRepo;
	
	@Autowired
	ImageService imageService;

	@Autowired
	AllocationRepository alloRepo;
	
	@Autowired
	PortfolioHistoryRepository histRepo;
	
	@Autowired
	PlatformTransactionManager transactionManager;

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
	TransactionTemplate template;
	Boolean force;

	public PortfolioEntryGrid(ManagePortfolioView v) {
		this(v,false);
	}
	
	public PortfolioEntryGrid(ManagePortfolioView v, Boolean f) {
		super(null, FilterMode.None);
		//this.listOperationProvider = new PortfolioEntriesListProvider();
		this.portfolio = v.getEntity();
		LOGGER.info("PortfolioBalancesList: {}", this.portfolio);
		this.showDeleteButton = false;
		this.showEditButton=false;
		this.appendButtonColumn = false;
		this.view = v;
		//this.entries = new HashSet<PortfolioEntryHistory>();
		this.showHeader = true;
		this.force = f;
	}

	@PostConstruct
	public void postConstruct() {
		
		this.template = new TransactionTemplate(transactionManager);
		
		template.executeWithoutResult(action -> {
	
			PortfolioHistory latest = portfolio.getLatest();
			if(force || latest == null || ZonedDateTime.now().minusMinutes(2).isAfter(latest.getTimestamp())) {
				summary = portService.createHistory(portfolio);
			}
			else {
				summary = histRepo.getById(latest.getId());
			}
			entries = summary.getEntries();
			BigDecimal tot = summary.getValue();
			dataProvider = new ListDataProvider<>(getEntries());
			super.postConstruct();
	
			totalValueCell.setText(ReserveFormat.format(tot));
			
			advancedButton.setVisible(false);
			count.setVisible(false);
			filter.setVisible(false);
			
			//this.grid.setMaxHeight("100%");
			//this.grid.setHeight(null);
			
			
			if(this.dataProvider.size(new Query()) == 0) {
				/*HeaderRow hr = this.grid.appendHeaderRow();
				HeaderCell hc = hr.join(grid.getColumns().toArray(new Column[0]));
				Span addBalances = new Span("Summary will be populated after adding currency balances on the Balances tab");
				hc.setComponent(addBalances);*/
			}
			
		});
		
		/*Button addBalance = new Button("Add Balance");
		countAndAdvanced.add(addBalance);*/
		

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
	/*
		@Override
		public Long getCountAll() {
			return (long) dataProvider.size(new Query<>());
		}*/

	
	
	
	/*static String[] WARN_STATUS = new String[] { 
			"Over Threshold", 
			"Near Threshold", 
			"Balanced",
			"Balanced", 
			"Balanced"
	};*/


	/*@Override
	public PagingAndSortingRepository<PortfolioEntryHistory, Long> getRepository() {
		return null;
	}*/

	/*	@Override
		public Long getEntityId(PortfolioEntryHistory entity) {
			if(entity.getCurrency()==null) return -1l;
			return entity.getCurrency().getId();
		}
	
		@Override
		public String getEntityName(PortfolioEntryHistory t) {
			return "";
		}*/


	protected void adjustAllocation(PortfolioEntryHistory pe) {

		Optional<Allocation> allo = alloRepo.findByPortfolioAndCurrency(portfolio, pe.getCurrency());

		LOGGER.info("current allocation: {}",allo);
		AllocationDialog dialog;
		if(!allo.isPresent()) {
			LOGGER.info("portfolio: {}",pe.getPortfolio());
			LOGGER.info("currency: {}",pe.getCurrency());
			//using the default allocation
			dialog = this.appCtx.getBean(AllocationDialog.class,portfolio,pe.getCurrency());
		}
		else {
			//Allocation a = alloRepo.findById(allo.get().getId()).get();
			Allocation a = allo.get();
			//allo = alloRepo.findById(allo.getId()).get();
			dialog = this.appCtx.getBean(AllocationDialog.class,a);
		}
		dialog.addOpenedChangeListener(changed -> {
			if(dialog.getEntityForm().getSaved()) {
				//this.view.refreshValueRelatedTabs();
				this.view.refreshSummaryTab(true);
				this.view.refreshAllocationTab();
				//view.runHistoryJob();
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

		Column<?> sym = grid.addColumn(BinderUtils.nestedValueDefault(PortfolioEntryHistory::getCurrency, Currency::getKey,"Unallocated"))
				.setHeader("Currency")
				.setWidth("90px")
				.setFlexGrow(0)
				.setSortProperty("currency.symbol");
		UiComponents.iconLabelColumn(sym);
		
		
		Column<PortfolioEntryHistory> allocationColumn = grid.addComponentColumn(entry -> {
			Span node = new Span();
			String perc = PercentFormat.format(entry.getAllocationPercent());
			if(entry.getAllocationUndefined()) {
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
				.comparing(PortfolioEntryHistory::getAllocationPercent))
		.setWidth("100px")
		.setFlexGrow(0);
		;

		
		grid.addColumn(entry -> {
			int sign = entry.getAdjustPercent().signum();
			Integer warnLevel = getWarnLevel(entry.getToTriggerPercent());
			String buySell = sign < 0? "Sell":"Buy";
			if(warnLevel == 0) {
				return "Over Threshold ("+buySell+")";
			}
			else if (warnLevel == 1 || warnLevel == 2) {
				return "Near Threshold ("+buySell+")";
			}
			//return "Balanced ("+buySell+")";
			return "Balanced";
		})
		.setHeader("Status")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator.comparing(PortfolioEntryHistory::getToTriggerPercentOrZero))
		.setWidth("150px")
		.setFlexGrow(0)
	;

		
		grid.addColumn(entry -> {
			BigDecimal adjust = entry.getAdjust();
			if(adjust == null) return null;
			LOGGER.info("currency: {}",entry.getCurrency());
			Integer dp = entry.getCurrency()!=null?entry.getCurrency().getDecimalPoints():(Integer)2;
			if(dp == null || dp > MAX_SCALE) dp = MAX_SCALE;
			if(dp != null && adjust.scale() > dp) {
				adjust = adjust.setScale(dp, RoundingMode.HALF_EVEN);
			}
			return adjust.toString();
		})
		.setHeader("Adjust ©")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator.comparing(PortfolioEntryHistory::getAdjust))
		.setWidth("125px")
		.setFlexGrow(0);
		
		
		grid.addColumn(PortfolioEntryHistory::getBalance)
		.setSortProperty("balance")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator.comparing(PortfolioEntryHistory::getBalance))
		.setHeader("Balance ©")
		.setAutoWidth(true)
		.setWidth("125px")
		.setFlexGrow(0);
	
		
		grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getAdjustReserve, ReserveFormat))
		.setHeader("Adjust $")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator.comparing(PortfolioEntryHistory::getAdjustAbsolute))
		.setWidth("125px")
		.setFlexGrow(0);
		
		

	

		Column<?> valueColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getValueReserve, ReserveFormat))
			.setHeader("Value $")
			.setTextAlign(ColumnTextAlign.END)
			.setSortProperty("value")
			.setComparator(Comparator.comparing(PortfolioEntryHistory::getValueReserve))
			.setWidth("110px")
			.setFlexGrow(0);


		
		
		grid.addComponentColumn(e -> {

			HorizontalLayout buttons = new HorizontalLayout();
			buttons.addClassNames("grid-buttons","allocation-buttons");
			buttons.setPadding(true);
			buttons.setWidthFull();
			buttons.setJustifyContentMode(JustifyContentMode.END);

			if(e.getCurrency()!=null) {
				Button allocationButton = new Button(VaadinIcon.PENCIL.create());
				allocationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				buttons.add(allocationButton);
				UIUtils.setTitle(allocationButton, "Allocation");
				allocationButton.addClickListener(click -> adjustAllocation(e));
				allocationButton.addClassName("link");
				buttons.add(allocationButton);

			}
			return buttons;
		})
		.setFlexGrow(0)
		.setClassNameGenerator(val -> {
			return HoverColumn.HOVER_COLUMN_CLASS;
		})
		.setWidth("40px");
		

		

	
		

		

		



		
		

		grid.addColumn(e -> {
			return "";
		});

		grid.setClassNameGenerator(pe -> {
			List<String> names = new ArrayList<>();
			Integer warnLevel = getWarnLevel(pe.getToTriggerPercent());
			names.add("warn-"+warnLevel);
			return StringUtils.join(names, " ");
		});

		footer = grid.appendFooterRow();
		totalValueCell = footer.getCell(valueColumn);
		

		
		HeaderRow hr = grid.prependHeaderRow();
		
		HeaderCell allocationHeader = hr.getCell(allocationColumn);
		
		Button resetAllocations = new Button("Detect",VaadinIcon.REFRESH.create(),click -> {
			ConfirmDialogs.yesNoDialog("Detect and Reset Allocations?", () -> {
				balanceServ.autoDetectAllocations(portfolio, summary);
				this.view.refreshSummaryTab(true);
				this.view.refreshAllocationTab();
			})
			.withModal(true)
			.withMessage("This will reset all allocations to their automatically detected values.")
			.open();
			;
		});
		resetAllocations.setIconAfterText(true);
		resetAllocations.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		HorizontalLayout hl = new HorizontalLayout(resetAllocations);
		hl.setWidthFull();
		hl.setAlignItems(Alignment.END);
		hl.setJustifyContentMode(JustifyContentMode.END);
		allocationHeader.setComponent(hl);
		
	}
	
	public static Integer getWarnLevel(BigDecimal bd) {
		if(bd != null) {
			for (int i = 0; i < WARN.length; i++) {
				if (bd.compareTo(WARN[i]) >= 0) {
					return i;
				}
			}
		}
		return WARN.length-1;
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
		
		Column<PortfolioEntryHistory> priceColumn = grid.addColumn(new NumberRenderer<>(BinderUtils.nestedValue(PortfolioEntryHistory::getRate, MarketRate::getRate),ReserveFormat))
		.setHeader("Price")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator.comparing(BinderUtils.nestedValue(PortfolioEntryHistory::getRate, MarketRate::getRate)))
		.setWidth("100px")
		.setFlexGrow(0);
		coll.add(priceColumn);
		
		Column<PortfolioEntryHistory> alertColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntryHistory::getToTriggerPercent, PercentFormat))
		.setHeader("Alert %")
		.setTextAlign(ColumnTextAlign.END)
		.setComparator(Comparator.comparing(PortfolioEntryHistory::getToTriggerPercentOrZero))
		.setWidth("110px")
		.setFlexGrow(0);
		coll.add(alertColumn);	
		
		GridUtils.reorderColumns(grid, c,6,adjustColumn,8,priceColumn,4,alertColumn,11);
		
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
	public DataProvider<PortfolioEntryHistory, ?> createBaseDataProvider() {
		ListDataProvider<PortfolioEntryHistory> ldp = new ListDataProvider<>(getEntries());
		ldp.setSortOrder(PortfolioEntryHistory::getToTriggerPercentOrZero, SortDirection.DESCENDING);
		return ldp;

	}

	public Set<PortfolioEntryHistory> getEntries() {
		return entries;
	}

	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<PortfolioEntryHistory, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	
}
