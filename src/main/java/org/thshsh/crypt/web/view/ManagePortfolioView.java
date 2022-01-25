package org.thshsh.crypt.web.view;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Activity;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.serv.PortfolioHistoryService;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.security.UnauthorizedException;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.BasicTabSheet;
import org.thshsh.vaadin.UIUtils;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.plotoptions.Treemap;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "portfolio", layout = MainLayout.class)
@CssImport("./styles/portfolio.css")
//@ScreenName("Portfolio")
public class ManagePortfolioView  extends VerticalLayout implements HasUrlParameter<String>, HasDynamicTitle {

	public static final Logger LOGGER = LoggerFactory.getLogger(ManagePortfolioView.class);
	
	public static final Logger LOGGER_ACTIVITY = LoggerFactory.getLogger(Activity.class);

	public static final String ID_PARAM = "id";
	public static final String SILENCE_PARAM = "silence";

	public static final String ICON_SIZE = "24px";
	
	public static final String ICON_SIZE_SMALL = "16px";

	@Autowired
	PortfolioHistoryService historyService;
	
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

	@Autowired
	PlatformTransactionManager transactionManager;

	TransactionTemplate template;

	Long entityId;
	Portfolio entity;

	//Boolean create = false;
	//NestedOrderedLayout<?> formLayout;
	
	VerticalLayout mainLayout;
	DistributionChart distroChart;
	PortfolioValueChart valueChart;
	BalanceChart balChart;
	
	BasicTabSheet tabSheet;
	Tab mainTab;
	Tab distributionTab;
	Tab chartTab;
	Tab balanceChartTab;
	Tab allocationTab;
	PortfolioEntryGrid entriesList;
	

	//static NumberFormat usdFormat = new DecimalFormat("$#,##0.00");

	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {

		

		this.setHeight("100%");

	    Location location = event.getLocation();
	    QueryParameters queryParameters = location.getQueryParameters();
	    Map<String, List<String>> parametersMap = queryParameters.getParameters();
	    if(parametersMap.containsKey(ID_PARAM)) {
	    	
	    	template.executeWithoutResult(action -> {
	    		
	    		entityId = Long.valueOf(parametersMap.get(ID_PARAM).get(0));
		    	entity = portRepo.findByIdSecured(entityId);
		    	if(entity == null) throw new UnauthorizedException();
		    	LOGGER.info("Got entity with id: {} = {}",entityId,entity);
		    	
		    	
		    	if(parametersMap.containsKey(SILENCE_PARAM)) {
		    		
			    		try {
				    	String param = parametersMap.get(SILENCE_PARAM).get(0);
				    	LOGGER.info("silence for duration: {}",param);
				    	Duration duration = Duration.parse("P"+param.toUpperCase());
				    	LOGGER.info("silence for duration: {}",duration);
				    	ZonedDateTime now = ZonedDateTime.now();
				    	ZonedDateTime then = now.plus(duration);
				    	//PrettyTime prettyTime = new PrettyTime(LocalDateTime.now());
				    	entity.getSettings().setSilentTill(then);
				    	
				    	String thenString = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(then);
				    	
				    	Span s = new Span("Silenced Portfolio Until "+thenString);
				    	s.setWidth("300px");
				    	Notification n = new Notification(s);
				    	n.setPosition(Position.MIDDLE);
				    	n.setDuration(2500);
				    	n.open();

			    		//entity.getSettings().setSilentTill(then);	
		    		}
		    		catch(DateTimeParseException e) {
		    			LOGGER.warn("Could not silence",e);
		    		}

			    }

	    		
	    		//return entity;
	    	});
	    	

	    }
	    
	    

	    LOGGER_ACTIVITY.info("User: {} Opened Portfolio: {}",AppSession.getCurrentUser().getUserName(),entity.getName());
	    
	    tabSheet = new BasicTabSheet();
	    tabSheet.getContentLayout().setMargin(false);
	    tabSheet.getContentLayout().setPadding(false);

	    tabSheet.setHeightFull();
	    tabSheet.getContentLayout().setHeight("100%");

	    //AreaChartExample ex = new AreaChartExample();
	    //tabSheet.addTab(new Tab("Chart"), ex);



	    //tabSheet.getContentLayout().add(new AreaChartExample());

	    mainLayout = createMainTab(false);
	    
	    HorizontalLayout summaryTabButton = new HorizontalLayout();
	    summaryTabButton.setSpacing(false);
	    summaryTabButton.setAlignItems(Alignment.CENTER);
	    summaryTabButton.add(new Span("Summary"));
	    PressButton pb = new PressButton(VaadinIcon.ELLIPSIS_CIRCLE_O.create());
	    pb.addClickListener(click -> {
	    	entriesList.showAdvanced(pb.getPressed());
	    	pb.setIcon(pb.getPressed()?VaadinIcon.ELLIPSIS_CIRCLE.create():VaadinIcon.ELLIPSIS_CIRCLE_O.create());
	    	
	    });
	    UIUtils.setTitle(pb, "Toggle Advanced View");
	    
	
	    pb.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
	    summaryTabButton.add(pb);
	    mainTab = tabSheet.addTab(summaryTabButton, mainLayout);
	   
	    VerticalLayout balancesLayout = createBalancesTab();
	    tabSheet.addTab(new Tab("Balances"), balancesLayout);
	    
	    if(SecurityUtils.hasAccess(Feature.Portfolio, Access.Super)) {
	    	  VerticalLayout alLlayout = createAllocationsTab();
	    	  allocationTab =  tabSheet.addTab(new Tab("Allocations"), alLlayout);
	    }

	   // Tab allTab = new Tab("Allocations");
	    //VerticalLayout allLayout = createAllocationsTab();
	    //tabSheet.addTab(new Tab("Allocations"), allLayout);

	    //Tab funcTab = new Tab("Functions");
	  

	   

	    //VerticalLayout chartsLayout = createChartsTab();
	    //tabSheet.addTab(new Tab("Charts"), chartsLayout);

	    valueChart = appContext.getBean(PortfolioValueChart.class,entity);
	    chartTab = tabSheet.addTab(new Tab("Value"), valueChart);
	    
	    balChart = appContext.getBean(BalanceChart.class,entity);
	    if(SecurityUtils.hasAccess(Feature.System, Access.Read)) {
	    	balanceChartTab = tabSheet.addTab(new Tab("Balance"), balChart);
	    }
	    
	    //brand new charts need to be set visible for them to render correctly
	    valueChart.setVisible(true);
	    

		/*   TreeMapChart balanceChart = new TreeMapChart();
		Tab balanceTab = tabSheet.addTab(new Tab("Balance"), balanceChart);
		balanceChart.setVisible(true);*/
	    
	    distroChart = new DistributionChart();
	    distributionTab = tabSheet.addTab(new Tab("Distribution"), distroChart);
	    distroChart.setVisible(true);
	
	    
	    VerticalLayout settingsLayout = createSettingsTab();
	    tabSheet.addTab(new Tab("Settings"), settingsLayout);
	    
	    if(SecurityUtils.hasAccess(Feature.System, Access.ReadWrite)) {
	    	VerticalLayout funcLayout = createFunctionsTab();
	    	tabSheet.addTab(new Tab("Functions"), funcLayout);
	    }
	    
	    add(tabSheet);
	    //add(new AreaChartExample());

		/* VerticalLayout outer = new VerticalLayout();
		outer.setSizeFull();
		outer.add(new AreaChartExample());
		add(outer);*/

	   


	    breadcrumbs
	    //.resetBreadcrumbs()
	    //.addBreadcrumb(PortfoliosView.TITLE, PortfoliosView.class)
	    .addBreadcrumb(entity.getName(), null)
	    ;
	    
	    
	}

	@PostConstruct
	public void postConstruct() {
		template = new TransactionTemplate(transactionManager);
	}

	protected void refreshValueChart() {
		 PortfolioValueChart newChartTab = appContext.getBean(PortfolioValueChart.class,entity);
		 tabSheet.replaceTab(chartTab, newChartTab);
		 newChartTab.setVisible(true);
		 this.valueChart = newChartTab;
	}

	protected void refreshAllocationTab() {
		
		if(SecurityUtils.hasAccess(Feature.Portfolio, Access.Super)) {
			VerticalLayout alLlayout = createAllocationsTab();
	  	    //tabSheet.addTab(new Tab("Allocations"), alLlayout);
	  	    tabSheet.replaceTab(allocationTab, alLlayout);
			//this.mainLayout = newMainTab;
	    }
		
	}
	
	protected void refreshValueRelatedTabs() {
		
		refreshSummaryTab(true);
		
		refreshDistributionChart();

		refreshValueChart();
		
		
	}
	
	public void refreshDistributionChart() {
		DistributionChart newDistroChart = new DistributionChart();
		tabSheet.replaceTab(distributionTab, newDistroChart);
		newDistroChart.setVisible(true);
		this.distroChart = newDistroChart;
	}
	
	protected void refreshSummaryTab(boolean force) {
		
		LOGGER.info("refreshMainTab visible: {}",mainTab.isVisible());
		VerticalLayout newMainTab = createMainTab(force);
		newMainTab.setVisible(this.mainLayout.isVisible());
		tabSheet.replaceTab(mainTab, newMainTab);
		this.mainLayout = newMainTab;

	}

	

	protected VerticalLayout createMainTab(Boolean force) {

		LOGGER.info("createMainTab");

		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		layout.setMargin(false);

		if(balanceRepo.countByPortfolio(entity)==0) {
			Span addBalances = new Span("Summary will be populated after adding balances on the Balances tab");
			addBalances.addClassNames("helper-text","highlighted");
			layout.add(addBalances);
		}
		else {
			entriesList = appContext.getBean(PortfolioEntryGrid.class,this,force);
			entriesList.setPadding(false);
			entriesList.setMargin(false);
			entriesList.setHeight("100%");
			layout.add(entriesList);
			layout.setHeight("100%");
		}

		return layout;

	}

	PortfolioBalanceGrid balancesList;

	PortfolioAllocationGrid allocationList;

	protected VerticalLayout createBalancesTab() {
		LOGGER.info("createBalancesTab");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
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
	
	protected void runHistoryJob() {
		historyService.runHistoryJob(entity);
	}

	protected VerticalLayout createFunctionsTab() {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setVisible(false);


		Button runJob = new Button("Run History Job",click -> {
			runHistoryJob();
		});
		layout.add(runJob);

		Button clearHistory = new Button("Clear History",click -> {
			this.template.executeWithoutResult(action -> {
				histRepo.deleteAllByPortfolio(entity);
				refreshValueChart();
			});

		});
		layout.add(clearHistory);
		
		


		return layout;
	}

	protected VerticalLayout createSettingsTab() {
		VerticalLayout layout = new VerticalLayout();
		layout.setVisible(false);


		PortfolioSettingsForm sf = appContext.getBean(PortfolioSettingsForm.class,this,this.entity);
		layout.add(sf);

		return layout;
	}

	/*protected VerticalLayout createChartsTab() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
	
		ZonedDateTime zdt = ZonedDateTime.now().minusDays(30);
		 
		List<PortfolioHistory> ph = histRepo.findByPortfolioAndTimestampGreaterThanOrderByTimestampAsc(entity, zdt);
	
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
	}*/



	//DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
	
	public class DistributionChart extends Div {
		
		 public DistributionChart() {

		    	//List<PortfolioEntry> entries = entriesList.entries;
		    	//List<BigInteger> values = new ArrayList<BigInteger>();
		    	//List<String> labels = new ArrayList<String>();
		    	List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		    	
		    	List<Double> series = new ArrayList<>();
		    	List<String> labels = new ArrayList<>();
		    	List<String> colors = new ArrayList<>();
		    	
		    	List<PortfolioEntryHistory> sorted = new ArrayList<PortfolioEntryHistory>(
		    			entriesList!=null?
		    			entriesList.entries:
		    			Collections.emptyList()
		    			);
		    	Collections.sort(sorted, (pe0,pe1) -> {
		    		return pe1.getValueReserve().compareTo(pe0.getValueReserve());
		    	});
		    	
		    	sorted.forEach(entry -> {
		    		
		    		if(entry.getCurrency()!=null) {
			    		Map<String,Object> m = new HashMap<>();
			    		m.put("x", entry.getCurrency().getKey());
			    		m.put("y", entry.getValue().toBigInteger());
			    		data.add(m);
			    		labels.add(entry.getCurrency().getName());
			    		series.add(entry.getValue().toBigInteger().doubleValue());
			    		colors.add("#"+entry.getCurrency().getColorHex());
		    		}
		    	});
		    	
		    	LOGGER.info("colors: {}",colors);
		    	
		    /*	List<PortfolioHistory> ph = histRepo.findByPortfolioOrderByTimestampAsc(entity);

		    	List<String> dates = new ArrayList<>();
				List<BigInteger> valuePerHour = new ArrayList<>();
				List<BigDecimal> thresh = new ArrayList<>();


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
	*/
				//List<BigInteger> values = valuePerHour;
				//List<String> labels = dates;

				//LOGGER.info("labels: {}",labels);


		        ApexCharts chart = ApexChartsBuilder.get()
		        		
		                .withChart(ChartBuilder.get()
		                		
		                        .withType(Type.pie)
		                        //.withZoom(ZoomBuilder.get()
		                                //.withEnabled(false)
		                                //.build()
		                          //)
		                        .build())
		                
		                .withLabels(labels.toArray(new String[0]))
		                
						/*.withDataLabels(DataLabelsBuilder.get()
						        .withEnabled(false)
						        .build())
						*/	                
		                
						/*.withStroke(StrokeBuilder.get()
								.withCurve(Curve.smooth)
								.withColors("var(--money-green)"
										,"var(--lumo-accent-color-2)"
										)
								.withWidth(3d)
								.build()
								)*/
						/*.withFill(FillBuilder.get().withOpacity(0d).build())*/
		                
		                
		                //.withSeries(new Series<>("STOCK ABC", 10.0, 41.0, 35.0, 51.0, 49.0, 62.0, 69.0, 91.0, 148.0))
		                //.withSeries(new Series<>("USD Value", values.toArray()),
		                //new Series<>("Alert Threshold", thresh.toArray()))
		                .withSeries(
		                		//new Series<>(data.toArray())
		                		series.toArray(new Double[0])
		                		)
		                .withColors(colors.toArray(new String[] {})) 
		                
		                //.withPlotOptions(PlotOptionsBuilder
		                	//	.get()
		                		//.withTreemap(new Treemap(true, false))
		                		//.build())
		                
		                //SeriesBuilder.get().withData(data)
		                
		                //.withDataLabels(DataLabelsBuilder.get().withFormatter(formatter).build())
		                //.withDataLabels(DataLabelsBuilder.get().withFormatter("'dd/MM/yyyy hh:mm'").build())
		                //.withDataLabels(DataLabelsBuilder.get().withFormatter(DatetimeFormatterBuilder.get().build()).build())

		                //DataLabelsBuilder.get().withFormatter(DatetimeFormatterBuilder.get().build())
		                //.withTitle(TitleSubtitleBuilder.get()
		                        //.withText("Fundamental Analysis of Stocks")
		                        //.withAlign(Align.left).build())
//		                .withSubtitle(TitleSubtitleBuilder.get()
		//                        .withText("Price Movements")
		  //                      .withAlign(Align.left).build())
		                //.withLabels(IntStream.range(1, 10).boxed().map(day -> LocalDate.of(2000, 1, day).toString()).toArray(String[]::new))
		                //.withStroke(StrokeBuilder.get().withColors("var(--lumo-accent-color-1)").build())
		              // .withLabels(labels.toArray(new String[labels.size()]))

						/*.withXaxis(XAxisBuilder.get()
						        .withType(XAxisType.datetime)
						
						        //.withTickAmount(new BigDecimal(50))
						        .withLabels(LabelsBuilder
						        		.get()
						        		//.withFormat("MMM dd hh:mm TT")
						        		.withFormat("MMM dd")
						        		//.withDateTimeUTC(false)
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
						        //.withMax(15000d)
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
						.withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())*/

		                //.withMarkers(MarkersBuilder.get().withSize(20d, 20d).build())

		                //.withPlotOptions(PlotOptions)


		                .build();
		        add(chart);
		        chart.setHeight("600px");
		        setWidth("100% ");
		        setHeight("800px");

		    }
		
	}
	
	public class TreeMapChart extends Div {
	    public TreeMapChart() {

	    	//List<PortfolioEntry> entries = entriesList.entries;
	    	//List<BigInteger> values = new ArrayList<BigInteger>();
	    	//List<String> labels = new ArrayList<String>();
	    	List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
	    	List<String> colors = new ArrayList<>();
	    	
	    	
	    	List<PortfolioEntryHistory> sorted = new ArrayList<PortfolioEntryHistory>(
	    			entriesList!=null?
	    			entriesList.entries:
	    			Collections.emptyList()
	    			);
	    	Collections.sort(sorted, (pe0,pe1) -> {
	    		return pe1.getValueReserve().compareTo(pe0.getValueReserve());
	    	});
	    	
	    	sorted.forEach(entry -> {
	    		
	    		
	    		Map<String,Object> m = new HashMap<>();
	    		m.put("x", entry.getCurrency().getKey());
	    		m.put("y", entry.getValue().toBigInteger());
	    		data.add(m);
	    		colors.add("#"+entry.getCurrency().getColorHex());
	    	});
	    	
	    	LOGGER.info("colors: {}",colors);
	    	
	    /*	List<PortfolioHistory> ph = histRepo.findByPortfolioOrderByTimestampAsc(entity);

	    	List<String> dates = new ArrayList<>();
			List<BigInteger> valuePerHour = new ArrayList<>();
			List<BigDecimal> thresh = new ArrayList<>();


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
*/
			//List<BigInteger> values = valuePerHour;
			//List<String> labels = dates;

			//LOGGER.info("labels: {}",labels);


	        ApexCharts chart = ApexChartsBuilder.get()
	        		
	                .withChart(ChartBuilder.get()
	                		
	                        .withType(Type.treemap)
	                        //.withZoom(ZoomBuilder.get()
	                                //.withEnabled(false)
	                                //.build()
	                          //)
	                        .build())
	                
					/*.withDataLabels(DataLabelsBuilder.get()
					        .withEnabled(false)
					        .build())
					*/	                
	                
					/*.withStroke(StrokeBuilder.get()
							.withCurve(Curve.smooth)
							.withColors("var(--money-green)"
									,"var(--lumo-accent-color-2)"
									)
							.withWidth(3d)
							.build()
							)*/
					/*.withFill(FillBuilder.get().withOpacity(0d).build())*/
	                
	                
	                //.withSeries(new Series<>("STOCK ABC", 10.0, 41.0, 35.0, 51.0, 49.0, 62.0, 69.0, 91.0, 148.0))
	                //.withSeries(new Series<>("USD Value", values.toArray()),
	                //new Series<>("Alert Threshold", thresh.toArray()))
	                .withSeries(
	                		new Series<>(data.toArray())
	                		)
	                .withColors(colors.toArray(new String[] {})) 
	                .withPlotOptions(PlotOptionsBuilder
	                		.get()
	                		.withTreemap(new Treemap(true, false))
	                		.build())
	                //SeriesBuilder.get().withData(data)
	                
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
	              // .withLabels(labels.toArray(new String[labels.size()]))

					/*.withXaxis(XAxisBuilder.get()
					        .withType(XAxisType.datetime)
					
					        //.withTickAmount(new BigDecimal(50))
					        .withLabels(LabelsBuilder
					        		.get()
					        		//.withFormat("MMM dd hh:mm TT")
					        		.withFormat("MMM dd")
					        		//.withDateTimeUTC(false)
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
					        //.withMax(15000d)
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
					.withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())*/

	                //.withMarkers(MarkersBuilder.get().withSize(20d, 20d).build())

	                //.withPlotOptions(PlotOptions)


	                .build();
	        add(chart);
	        chart.setHeight("400px");
	        setWidth("100%");
	        setHeight("400px");

	    }
	}
	
	
	//new Intl.NumberFormat().format(number)
	static String formatter = "function(val){var form = new Intl.NumberFormat(); var s = '$'+form.format(val); return s;}";
	static String percentFormatter = "function(val){return val*100 +'%';}";

	@Override
	public String getPageTitle() {
		// UI.getCurrent().getPage().setTitle("Manage Portfolio: "+entity.getName());
		return "Portfolio: "+entity.getName(); 
	}

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
