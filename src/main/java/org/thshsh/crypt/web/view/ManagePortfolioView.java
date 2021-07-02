package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.cryptman.BalanceRepository;
import org.thshsh.cryptman.Portfolio;
import org.thshsh.cryptman.PortfolioHistory;
import org.thshsh.cryptman.PortfolioHistoryRepository;
import org.thshsh.cryptman.PortfolioRepository;
import org.thshsh.vaadin.BasicTabSheet;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.Fill;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.FillBuilder;
import com.github.appreciated.apexcharts.config.builder.LegendBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.config.xaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "manage", layout = MainLayout.class)
@PageTitle("Manage Portfolio")
public class ManagePortfolioView  extends VerticalLayout implements HasUrlParameter<String> {

public static final Logger LOGGER = LoggerFactory.getLogger(EntityView.class);

	public static final String ID_PARAM = "id";

	public static final String ICON_SIZE = "24px";

	@Autowired
	ApplicationContext appContext;

	@Autowired
	Breadcrumbs breadcrumbs;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	BalanceRepository balanceRepo;

	@Autowired
	PortfolioHistoryRepository histRepo;

	@Autowired
	Scheduler scheduler;

	Long entityId;
	Portfolio entity;

	//Boolean create = false;
	//NestedOrderedLayout<?> formLayout;
	BasicTabSheet tabSheet;
	VerticalLayout mainLayout;
	Tab mainTab;
	//VerticalLayout pages;
	//Map<Tab, Component> tabsToPages;
	//Tab main;



	static NumberFormat usdFormat = new DecimalFormat("$#,##0.00");

	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {

		this.setHeight("100%");

	    Location location = event.getLocation();
	    QueryParameters queryParameters = location.getQueryParameters();
	    Map<String, List<String>> parametersMap = queryParameters.getParameters();
	    if(parametersMap.containsKey(ID_PARAM)) {
	    	entityId = Long.valueOf(parametersMap.get(ID_PARAM).get(0));
	    	entity = portRepo.findById(entityId).get();
	    	LOGGER.info("Got entity with id: {} = {}",entityId,entity);
	    }

	    tabSheet = new BasicTabSheet();
	    tabSheet.getContentLayout().setMargin(false);
	    tabSheet.getContentLayout().setPadding(false);

	    tabSheet.setHeightFull();
	    tabSheet.getContentLayout().setHeight("100%");

	    //AreaChartExample ex = new AreaChartExample();
	    //tabSheet.addTab(new Tab("Chart"), ex);



	    //tabSheet.getContentLayout().add(new AreaChartExample());

	    mainLayout = createMainTab();
	    mainTab = tabSheet.addTab("Portfolio", mainLayout);
	    //mainLayout = createMainTab();
	    //tabSheet.addTab(new Tab("Portfolio"), mainLayout);


	   // Tabs tabs = new Tabs();
	    //add(tabs);

	    //main = new Tab("Portfolio");
	   // tabs.add(main);

	    //mainLayout = createMainTab();
	    //mainLayout.setVisible(true);

	   // Tab balTab = new Tab("Balances");
	    VerticalLayout balancesLayout = createBalancesTab();
	    tabSheet.addTab(new Tab("Balances"), balancesLayout);

	   // Tab allTab = new Tab("Allocations");
	    VerticalLayout allLayout = createAllocationsTab();
	    tabSheet.addTab(new Tab("Allocations"), allLayout);

	    //Tab funcTab = new Tab("Functions");
	    VerticalLayout funcLayout = createFunctionsTab();
	    tabSheet.addTab(new Tab("Functions"), funcLayout);

	    //VerticalLayout chartsLayout = createChartsTab();
	    //tabSheet.addTab(new Tab("Charts"), chartsLayout);

	    ValueChart ex2 = new ValueChart();
	    tabSheet.addTab(new Tab("Chart"), ex2);
	    ex2.setVisible(true);
	    //ex2.addClassName("invisible");

		/* tabsToPages = new HashMap<>();
		tabsToPages.put(main,mainLayout);
		tabsToPages.put(balTab,balancesLayout);
		tabsToPages.put(allTab,allLayout);
		tabsToPages.put(funcTab,funcLayout);

		Tabs tabs = new Tabs(main,balTab,allTab,funcTab);
		pages = new VerticalLayout(mainLayout,balancesLayout,allLayout,funcLayout);
		pages.setHeight("100%");

		tabs.addSelectedChangeListener(e -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
		    selectedPage.setVisible(true);
		});

		add(tabs, pages);
		*/
	    add(tabSheet);
	    //add(new AreaChartExample());

		/* VerticalLayout outer = new VerticalLayout();
		outer.setSizeFull();
		outer.add(new AreaChartExample());
		add(outer);*/


	    breadcrumbs.resetBreadcrumbs()
	    .addBreadcrumb(PortfoliosView.TITLE, PortfoliosView.class)
	    .addBreadcrumb(entity.getName(), null)
	    ;
	}

	protected void refreshMainTab() {
		LOGGER.info("refreshMainTab visible: {}",mainTab.isVisible());
		VerticalLayout mainLayout = createMainTab();
		mainLayout.setVisible(this.mainLayout.isVisible());
		tabSheet.replaceTab(mainTab, mainLayout);
		this.mainLayout = mainLayout;

		//pages.replace(this.mainLayout, mainLayout);
		//tabsToPages.put(main, mainLayout);
		//mainLayout.setVisible(this.mainLayout.isVisible());
		//this.mainLayout = mainLayout;

	}

	PortfolioEntryGrid entriesList;

	protected VerticalLayout createMainTab() {

		LOGGER.info("createMainTab");

		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		layout.setMargin(false);

		entriesList = appContext.getBean(PortfolioEntryGrid.class,this);
		entriesList.setPadding(false);
		entriesList.setMargin(false);
		entriesList.setHeight("100%");
		layout.add(entriesList);
		layout.setHeight("100%");

		/*//MutableBigDecimal mbd;
		Map<Currency,MutableBigDecimal> currencyBalances = new HashMap<>();

		balanceRepo.findByPortfolio(entity).forEach(bal -> {
			LOGGER.info("Balance: {}",bal);
			if(!currencyBalances.containsKey(bal.getCurrency())) currencyBalances.put(bal.getCurrency(), new MutableBigDecimal(0));
			currencyBalances.get(bal.getCurrency()).inc(bal.getBalance());
		});

		currencyBalances.forEach((cur,bal)-> {
			layout.add(new Span(cur.getSymbol()+": "+bal.getAsBigDecimal()));

		});*/

		return layout;

	}

	PortfolioBalanceGrid balancesList;

	PortfolioAllocationGrid allocationList;

	protected VerticalLayout createBalancesTab() {
		LOGGER.info("createBalancesTab");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setPadding(false);
		balancesList = appContext.getBean(PortfolioBalanceGrid.class,this);
		balancesList.setHeight("100%");
		layout.add(balancesList);
		layout.setHeight("100%");
		return layout;
	}

	protected VerticalLayout createAllocationsTab() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setPadding(false);
		allocationList = appContext.getBean(PortfolioAllocationGrid.class,this);
		allocationList.setHeight("100%");
		layout.add(allocationList);
		layout.setHeight("100%");
		layout.setVisible(false);
		return layout;
	}

	protected VerticalLayout createFunctionsTab() {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setVisible(false);
		Button runJob = new Button("Run History Job",click -> {
			try {
				scheduler.getJobDetail(JobKey.jobKey(""));
				Trigger t = TriggerBuilder.newTrigger().forJob("history-job").startNow().build();
				scheduler.scheduleJob(t);
			}
			catch (SchedulerException e) {
				throw new IllegalStateException(e);
			}
		});
		layout.add(runJob);
		return layout;
	}

	protected VerticalLayout createSettingsTab() {
		VerticalLayout layout = new VerticalLayout();
		layout.setVisible(false);



		return layout;
	}

	protected VerticalLayout createChartsTab() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();

		List<PortfolioHistory> ph = histRepo.findByPortfolioOrderByTimestampAsc(entity);

		List<BigDecimal> valuePerHour = new ArrayList<>();
		ph.forEach(hist -> {
			valuePerHour.add(hist.getValue());
		});


		 ApexCharts areaChart = ApexChartsBuilder.get()
	                .withChart(ChartBuilder.get()
	                        .withType(Type.line)
	                        .withZoom(ZoomBuilder.get()
	                                .withEnabled(false)
	                                .build())
	                        .build())
	                .withDataLabels(DataLabelsBuilder.get()
	                        .withEnabled(false)
	                        .build())
	                .withStroke(StrokeBuilder.get().withCurve(Curve.smooth).build())

	                .withSeries(new Series<>("Value", valuePerHour))
					 .withTitle(TitleSubtitleBuilder.get()
					         .withText("Fundamental Analysis of Stocks")
					         .withAlign(Align.left).build())
						                .withSubtitle(TitleSubtitleBuilder.get()
					                    .withText("Price Movements")
					                    .withAlign(Align.left).build())
					.withStroke(StrokeBuilder.get().withColors("var(--lumo-accent-color-1)").build())
	                .withLabels(IntStream.range(1, 10).boxed().map(day -> LocalDate.of(2000, 1, day).toString()).toArray(String[]::new))
	                //.withXaxis(XAxisBuilder.get().with)
	                .withXaxis(XAxisBuilder.get()
	                        .withType(XAxisType.datetime).build())
	                .withYaxis(YAxisBuilder.get()
	                        .withOpposite(true).build())
	                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
	                .build();
		 //areaChart.setHeight("100%");
		 areaChart.setSizeFull();
		 Div chart = new Div(areaChart);
		 chart.setSizeFull();
		 vl.add(chart);



		return vl;
	}



	DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	public class ValueChart extends Div {
	    public ValueChart() {

	    	List<PortfolioHistory> ph = histRepo.findByPortfolioOrderByTimestampAsc(entity);

	    	List<String> dates = new ArrayList<>();
			List<BigInteger> valuePerHour = new ArrayList<>();
			List<BigDecimal> thresh = new ArrayList<>();

			/*for(int i=0;i<ph.size();i++) {
				if(i%3 == 0) {
					PortfolioHistory hist = ph.get(i);
					if(hist.getValue()!=null) {
						valuePerHour.add(hist.getValue().toBigInteger());
						dates.add(dtf.format(hist.getTimestamp().withZoneSameInstant(ZoneId.systemDefault())));

					}
				}
			}*/

			ph.forEach(hist -> {
			//				valuePerHour.add(hist.getValue());
				if(hist.getValue()!=null) {
					valuePerHour.add(hist.getValue().toBigInteger());
					//LOGGER.info("zone: {}",ZoneId.systemDefault());
					thresh.add(hist.getMaxToTriggerPercent());
					dates.add(dtf.format(hist.getTimestamp().withZoneSameInstant(ZoneId.systemDefault())));
					//dates.add(dtf.format(hist.getTimestamp()));
					LOGGER.info("adding: {}",hist);

				}

			});

			LOGGER.info("valuePerHour: {}",valuePerHour);

			List<BigInteger> values = valuePerHour;
			List<String> labels = dates;

			LOGGER.info("labels: {}",labels);


	        ApexCharts areaChart = ApexChartsBuilder.get()

	                .withChart(ChartBuilder.get()
	                        .withType(Type.line)
	                        .withZoom(ZoomBuilder.get()
	                                .withEnabled(false)
	                                .build())
	                        .build())
	                .withDataLabels(DataLabelsBuilder.get()
	                        .withEnabled(false)
	                        .build())
	                .withStroke(StrokeBuilder.get()
	                		.withCurve(Curve.straight)
	                		.withColors("var(--lumo-accent-color-1)"
	                				,"var(--lumo-accent-color-2)"
	                				)
	                		.withWidth(3d)
	                		.build()
	                		)
	                .withFill(FillBuilder.get().withOpacity(0d).build())
	                //.withSeries(new Series<>("STOCK ABC", 10.0, 41.0, 35.0, 51.0, 49.0, 62.0, 69.0, 91.0, 148.0))
	                .withSeries(new Series<>("USD Value", values.toArray()),
	                new Series<>("Alert Threshold", thresh.toArray()))
	                //.withDataLabels(DataLabelsBuilder.get().withFormatter(formatter).build())
	                //.withDataLabels(DataLabelsBuilder.get().withFormatter("'dd/MM/yyyy hh:mm'").build())
	                //.withDataLabels(DataLabelsBuilder.get().withFormatter(DatetimeFormatterBuilder.get().build()).build())

	                //DataLabelsBuilder.get().withFormatter(DatetimeFormatterBuilder.get().build())
	                //.withTitle(TitleSubtitleBuilder.get()
	                        //.withText("Fundamental Analysis of Stocks")
	                        //.withAlign(Align.left).build())
//	                .withSubtitle(TitleSubtitleBuilder.get()
	//                        .withText("Price Movements")
	  //                      .withAlign(Align.left).build())
	                //.withLabels(IntStream.range(1, 10).boxed().map(day -> LocalDate.of(2000, 1, day).toString()).toArray(String[]::new))
	                //.withStroke(StrokeBuilder.get().withColors("var(--lumo-accent-color-1)").build())
	               .withLabels(labels.toArray(new String[labels.size()]))

	                .withXaxis(XAxisBuilder.get()
	                        .withType(XAxisType.datetime)

	                        //.withTickAmount(new BigDecimal(50))
	                        .withLabels(LabelsBuilder
	                        		.get()
	                        		//.withFormat("MMM dd hh:mm TT")
	                        		.withFormat("MMM dd")
	                        		.withDateTimeUTC(false)
	                        		.build())

	                        //.withLabels(LabelsBuilder.get()

	                        		//.withDatetimeFormatter(DatetimeFormatterBuilder.get()
	                        				//.withDay("dd")
	                        				//.withMonth("MM")
	                        				//.withHour("hh")
	                        				//.withMinute("mm")
	                        				//.build())

	                        		//.build())
	                        .build())
	                .withYaxis(YAxisBuilder.get()
	                        //.withOpposite(true)
//	                		.withAxisTicks(null)

	                		.withDecimalsInFloat(0d)
	                		.withTickAmount(10d)
	                        .withMin(0d)
	                        .withMax(15000d)
	                        .build()
	                        ,YAxisBuilder.get()
	                        	.withOpposite(true)
	                        	.withMax(1)
	                        	.withMin(0)
	                        	.withDecimalsInFloat(2d)
	                        	//.withLabels(com.github.appreciated.apexcharts.config.yaxis.builder.LabelsBuilder.get().withFormatter(percentFormatter).build())
	                        	.build()
	                        )
	               // .withYaxis(YAxisBuilder.get().withOpposite(true).build())
	                //.withYaxis(YAxisBuilder.get().)
	                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())

	                //.withMarkers(MarkersBuilder.get().withSize(20d, 20d).build())

	                //.withPlotOptions(PlotOptions)


	                .build();
	        add(areaChart);
	        areaChart.setHeight("600px");
	        setWidth("100%");
	        setHeight("600px");

	    }
	}

	//new Intl.NumberFormat().format(number)
	static String formatter = "function(val){var form = new Intl.NumberFormat(); var s = '$'+form.format(val); return s;}";
	static String percentFormatter = "function(val){return val*100 +'%';}";

	/*public class AreaChartExample2 extends Div {
	    public AreaChartExample2() {


	        ApexCharts areaChart = ApexChartsBuilder.get()

	                .withChart(ChartBuilder.get()
	                        .withType(Type.area)
	                        .withZoom(ZoomBuilder.get()
	                                .withEnabled(false)
	                                .build())
	                        .build())
	                .withDataLabels(DataLabelsBuilder.get()
	                        .withEnabled(false)
	                        .build())
	                .withStroke(StrokeBuilder.get().withCurve(Curve.straight).build())
	                .withSeries(new Series<>("STOCK ABC", 10.0, 41.0, 35.0, 51.0, 49.0, 62.0, 69.0, 91.0, 148.0))
	                //.withSeries(new Series<>("Value", values))
	                .withTitle(TitleSubtitleBuilder.get()
	                        .withText("Fundamental Analysis of Stocks")
	                        .withAlign(Align.left).build())
	                .withSubtitle(TitleSubtitleBuilder.get()
	                        .withText("Price Movements")
	                        .withAlign(Align.left).build())
	                .withLabels(IntStream.range(1, 10).boxed().map(day -> LocalDate.of(2000, 1, day).toString()).toArray(String[]::new))
	                .withXaxis(XAxisBuilder.get()
	                        .withType(XAxisType.datetime).build())
	                .withYaxis(YAxisBuilder.get()
	                        .withOpposite(true).build())
	                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())

	                .build();
	        add(areaChart);
	        areaChart.setHeight("400px");
	        setWidth("100%");
	        setHeight("400px");

	    }
	}*/

}
