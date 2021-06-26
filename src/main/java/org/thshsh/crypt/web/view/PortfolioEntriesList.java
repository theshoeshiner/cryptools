package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Currency;
import org.thshsh.cryptman.Allocation;
import org.thshsh.cryptman.AllocationRepository;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.ManagePortfolioService;
import org.thshsh.cryptman.MarketRate;
import org.thshsh.cryptman.MarketRateService;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioSummary;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.FunctionUtils;

import com.sun.mail.util.TraceInputStream;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.NumberRenderer;



@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class PortfolioEntriesList extends EntitiesList<PortfolioEntry, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioBalancesList.class);

	static NumberFormat ReserveFormat = new DecimalFormat("$#,##0.00");
	static NumberFormat PercentFormat = new DecimalFormat("##.###%");
	static NumberFormat CoinFormat = new DecimalFormat("####.########");

	@Autowired
	ApplicationContext appContext;

	@Autowired
	BalanceRepository balanceRepo;

	@Autowired
	AllocationRepository alloRepo;

	@Autowired
	MarketRateService rateService;

	@Autowired
	ManagePortfolioService portService;

	Portfolio portfolio;
	ManagePortfolioView view;

	List<PortfolioEntry> entries;

	ListDataProvider<PortfolioEntry> dataProvider;

	FooterCell totalValueCell;
	FooterCell totalAdjustCell;



	BigDecimal indThreshold = new BigDecimal(".15");
	BigDecimal portThreshold = new BigDecimal(".04");

	public PortfolioEntriesList(ManagePortfolioView v) {
		super(PortfolioEntry.class, null,FilterMode.None);
		this.listOperationProvider = new PortfolioEntriesListProvider();
		this.portfolio = v.entity;
		LOGGER.info("PortfolioBalancesList: {}",this.portfolio);
		this.showDeleteButton = true;
		this.showButtonColumn = false;
		this.view = v;
		this.entries = new ArrayList<PortfolioEntry>();
	}



	@PostConstruct
	public void postConstruct() {

		PortfolioSummary summary = portService.getSummary(portfolio);
		entries = summary.getEntries();
		BigDecimal tot = summary.getTotalValue();

		/*BigDecimal sum = alloRepo.findAllocationSumByPortfolio(portfolio);

		BigDecimal remainder = BigDecimal.ONE.subtract(sum);
		LOGGER.info("remainder: {}",remainder);
		Allocation remainderAllocation = new Allocation();
		MutableInt remainderCount = new MutableInt(0);


		List<Allocation> allocations = alloRepo.findByPortfolio(portfolio);
		Map<Currency,Allocation> allocationMap = allocations.stream().collect(Collectors.toMap(Allocation::getCurrency, Function.identity()));

		Map<Currency,MutableBigDecimal> currencyBalances = new HashMap<>();
		balanceRepo.findByPortfolio(this.portfolio).forEach(bal -> {
			LOGGER.info("Balance: {}",bal);
			if(!currencyBalances.containsKey(bal.getCurrency())) currencyBalances.put(bal.getCurrency(), new MutableBigDecimal(0));
			currencyBalances.get(bal.getCurrency()).inc(bal.getBalance());
		});

		LOGGER.info("currencyBalances: {}",currencyBalances.keySet());

		Map<Currency,MarketRate> rates = rateService.getUpToDateMarketRates(currencyBalances.keySet());
		LOGGER.info("got rates: {}",rates);

		Map<Currency,BigDecimal> currencyValues = new HashMap<>();
		Map<Currency,PortfolioEntry> entryMap = new HashMap<>();

		MutableBigDecimal totalValue = new MutableBigDecimal(0);

		currencyBalances.forEach((cur,bal)-> {
			BigDecimal value = bal.getAsBigDecimal().multiply(rates.get(cur).getRate());
			currencyValues.put(cur, value);
			totalValue.inc(value);
			Allocation a;
			if(allocationMap.containsKey(cur)) {
				a = allocationMap.get(cur);
			}
			else {
				a = remainderAllocation;
				remainderCount.increment();
			}
			PortfolioEntry pe = new PortfolioEntry(bal.getAsBigDecimal(), cur,a,rates.get(cur));
			pe.setValueReserve(value);
			entryMap.put(cur, pe);
			entries.add(pe);
		});

		BigDecimal remainderPer = remainder.divide(BigDecimal.valueOf(remainderCount.longValue()),4, RoundingMode.HALF_EVEN);
		LOGGER.info("{} / {} = {}", remainder,remainderCount,remainderPer);
		remainderAllocation.setPercent(remainderPer);

		LOGGER.info("remainderAllocation: {}",remainderAllocation);
		LOGGER.info("currencyBalances: {}",currencyBalances.keySet());

		LOGGER.info("total value: {}",totalValue.getAsBigDecimal());

		currencyBalances.forEach((cur,bal)-> {

			LOGGER.info("cur: {}",cur);

			PortfolioEntry pe = entryMap.get(cur);
			BigDecimal percent = pe.getAllocation().getPercent();
			BigDecimal targetValue = totalValue.getAsBigDecimal().multiply(percent);
			pe.setTargetReserve(targetValue);

			BigDecimal adjPerc = pe.getAdjustReserve().divide(totalValue.getAsBigDecimal(),RoundingMode.HALF_EVEN);
			LOGGER.info("adjPerc: {}",adjPerc);
			pe.setAdjustPercent(adjPerc);


			//thresh - 15/4
			BigDecimal thresh;
			BigDecimal ind = pe.getAllocation().getPercent().multiply(indThreshold);
			LOGGER.info("ind thresh: {}",ind);
			if(ind.compareTo(portThreshold) >0)  thresh = portThreshold;
			else thresh = ind;
			pe.setThresholdPercent(thresh);

			LOGGER.info("Threshold: {}",thresh);



			if(!pe.getAllocation().equals(remainderAllocation)) {
				BigDecimal toTrigger = adjPerc.divide(thresh, RoundingMode.HALF_EVEN);
				LOGGER.info("toTrigger: {}",toTrigger);
				pe.setToTriggerPercent(toTrigger);
			}

		});

		BigDecimal tot = totalValue.getAsBigDecimal();*/

		dataProvider =  new ListDataProvider<>(entries);

		super.postConstruct(appContext);

		//this.grid.setHeightByRows(true);
		totalValueCell.setText(ReserveFormat.format(tot));
		totalAdjustCell.setText(PercentFormat.format(summary.getTotalAdjustPercent()));

	}


	@Override
	public Dialog createDialog(PortfolioEntry entity) {
		LOGGER.info("createDialog: {}",this.portfolio);
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity,portfolio);
		return cd;
	}



	@Override
	public void refresh() {
		super.refresh();
	}



	@Override
	public Long getCountAll() {
		return (long)dataProvider.size(new Query<>());
	}

	//BigDecimal alert = BigDecimal.ONE;
	BigDecimal[] warn = new BigDecimal[] {
			new BigDecimal("1"),new BigDecimal(".75"),new BigDecimal(".5"),new BigDecimal(".25")
	};


	public class PortfolioEntriesListProvider extends DelegateEntitiesListProvider<PortfolioEntry,Long> {

		public PortfolioEntriesListProvider() {
			this.list = PortfolioEntriesList.this;
		}

		@Override
		public Long getEntityId(PortfolioEntry entity) {
			return entity.getCurrency().getId();
		}

		@Override
		public void setupColumns(Grid<PortfolioEntry> grid) {


			grid.addComponentColumn(entry -> {
				if(entry.getCurrency().getImageUrl()!=null) {
					String imageUrl = "image/"+entry.getCurrency().getImageUrl();
					Image image = new Image(imageUrl,"Icon");
					image.setWidth(ManagePortfolioView.ICON_SIZE);
					image.setHeight(ManagePortfolioView.ICON_SIZE);
					return image;
				}
				else return new Span();

			})
			.setWidth("48px")
			.setFlexGrow(0)
			//.setSortProperty("currency.symbol")
			.setClassNameGenerator(pe -> {
				return "icon";
			})
			;

			grid.addColumn(FunctionUtils.nestedValue(PortfolioEntry::getCurrency, Currency::getKey))
			.setHeader("Currency")
			.setWidth("110px")
			.setFlexGrow(1)
			.setSortProperty("currency.symbol")
			;

			grid.addColumn(new NumberRenderer<>(FunctionUtils.nestedValue(PortfolioEntry::getAllocation, Allocation::getPercent),PercentFormat))
			.setHeader("Allocation")
			.setSortProperty("allocation.percent")
			.setComparator(Comparator.comparing(FunctionUtils.nestedValue(PortfolioEntry::getAllocation, Allocation::getPercent)))
			.setWidth("100px")
			.setFlexGrow(0)
			;

			grid.addColumn(new NumberRenderer<>(FunctionUtils.nestedValue(PortfolioEntry::getRate, MarketRate::getRate), ReserveFormat))
			.setHeader("Rate")
			.setTextAlign(ColumnTextAlign.END)
			.setSortProperty("rate.rate")
			.setComparator(Comparator.comparing(FunctionUtils.nestedValue(PortfolioEntry::getRate, MarketRate::getRate)))
			.setWidth("100px")
			.setFlexGrow(0)
			;






			grid.addColumn(PortfolioEntry::getBalance)
			.setSortProperty("balance")
			.setComparator(Comparator.comparing(PortfolioEntry::getBalance))
			.setHeader("Balance")
			.setWidth("125px")
			.setFlexGrow(0)
			;

			Column<?> valueColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntry::getValueReserve,ReserveFormat))
			.setHeader("Value")
			.setTextAlign(ColumnTextAlign.END)
			.setSortProperty("value")
			.setComparator(Comparator.comparing(PortfolioEntry::getValueReserve))
			.setWidth("110px")
			.setFlexGrow(0)
			;

			grid.addColumn(new NumberRenderer<>(PortfolioEntry::getTargetReserve,ReserveFormat))
			.setHeader("Target")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntry::getTargetReserve))
			.setWidth("110px")
			.setFlexGrow(0)
			;



			grid.addColumn(new NumberRenderer<>(PortfolioEntry::getAdjustReserve,ReserveFormat))
			.setHeader("Adjust $")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntry::getAdjustAbsolute))
			.setWidth("125px")
			.setFlexGrow(0)
			;

			//CoinFormat

			//grid.addColumn(PortfolioEntry::getAdjust)
			grid.addColumn(new NumberRenderer<>(PortfolioEntry::getAdjust,CoinFormat))
			.setHeader("Adjust ©")
			//.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntry::getAdjust))
			//.setWidth("100px")
			//.setFlexGrow(0)
			.setWidth("125px")
			.setFlexGrow(0)
			;

			Column<?>  adjustColumn = grid.addColumn(new NumberRenderer<>(PortfolioEntry::getAdjustPercent,PercentFormat))
			.setHeader("Adjust %")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntry::getAdjustPercentAbsolute))
			.setWidth("110px")
			.setFlexGrow(0)
			;

			/*grid.addColumn(new NumberRenderer<>(PortfolioEntry::getThresholdPercent,PercentFormat))
			.setHeader("Trigger %")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntry::getThresholdPercent))
			.setWidth("100px")
			.setFlexGrow(0)
			;*/



			grid.addColumn(new NumberRenderer<>(PortfolioEntry::getToTriggerPercent,PercentFormat))
			.setHeader("Alert %")
			.setTextAlign(ColumnTextAlign.END)
			.setComparator(Comparator.comparing(PortfolioEntry::getToTriggerPercentOrZero))
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

				for(int i=0;i<warn.length;i++) {
					BigDecimal b = warn[i];
					if(pe.getToTriggerPercentOrZero().compareTo(b)>0) {
						names.add("warn-"+i);
						break;
					}
				}

				return StringUtils.join(names, " ");
				//return new StringJoiner(" ",)
			});




			FooterRow footer = grid.appendFooterRow();
			totalValueCell = footer.getCell(valueColumn);
			totalAdjustCell = footer.getCell(adjustColumn);


		}



		@Override
		public void setFilter(String text) {

		}

		@Override
		public void clearFilter() {

		}

		@Override
		public DataProvider<PortfolioEntry, ?> createDataProvider() {
			ListDataProvider<PortfolioEntry> ldp = new ListDataProvider<>(entries);
			ldp.setSortOrder(PortfolioEntry::getToTriggerPercentOrZero, SortDirection.DESCENDING);
			return ldp;

		}

		@Override
		public ExampleFilterRepository<PortfolioEntry, Long> getRepository() {
			return null;
		}

		@Override
		public String getEntityName(PortfolioEntry t) {
			return "";
			//return t.getBalance().toString() +" "+t.getCurrency().getSymbol();
			//return t.getExchange().getName() +" / "+t.getBalance().toString();
		}



	}

}

