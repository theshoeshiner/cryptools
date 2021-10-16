package org.thshsh.crypt.web.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.map.SingletonMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.FillBuilder;
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.DropShadowBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.fill.builder.GradientBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.RadialBarBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.hollow.HollowPosition;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.builder.HollowBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.builder.NameBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.builder.RadialBarDataLabelsBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.builder.TrackBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.builder.ValueBuilder;
import com.github.appreciated.apexcharts.config.stroke.LineCap;
import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat.RepeatMode;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

@Route(value = HomeView.PATH, layout = MainLayout.class)
@PageTitle(HomeView.TITLE)
//@RouteAlias(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(HomeView.class);

	public static final String PATH = "dashboard";
	public static final String TITLE = "Dashboard";

	public static final VaadinIcon ICON = VaadinIcon.DASHBOARD;

	@Autowired
	Breadcrumbs breadcrumbs;
	
	@Autowired
	AppSession session;

	@Autowired
	PortfolioRepository portRepo;
	
	@Autowired
	ApplicationContext context;
	
	//@Autowired
	//@PersistenceContext
	//AuditReader auditReader;

	//@Autowired
	//EntityManagerFactory entityManagerFactory;

    public HomeView() {
    	super();
    }


    @PostConstruct
    //@Transactional
    public void postConstruct() {
    	
    	breadcrumbs.resetBreadcrumbs().addBreadcrumb(TITLE, HomeView.class);
    	
    	
    	
		/*
		    	AuditQuery query =  auditReader
		    		    .createQuery()
		    		    .forRevisionsOfEntity( ProfileConfiguration.class, true, false )
		    		    .addOrder( AuditEntity.revisionProperty("timestamp").desc() );
		    	 query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());


		    	List<?> results =  query.getResultList();


		    	results.forEach(o -> {
		    		LOGGER.info("entity: {}",o);

		    	});
		    	*/

    	
    	List<Portfolio> ports = portRepo.findAllByUser(session.getUser());
    	
   
    	        FlexibleGridLayout layout = new FlexibleGridLayout()
    	        		
    	                .withColumns(RepeatMode.AUTO_FILL, new MinMax(new Length("110px"), new Flex(1)))
    	                .withAutoRows(new Length("110px"))
    	                /*.withItems(
    	                        new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(),
    	                        new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(),
    	                        new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard(), new ExampleCard()
    	                )*/
    	                .withPadding(true)
    	                .withSpacing(true)
    	                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
    	                .withOverflow(GridLayoutComponent.Overflow.AUTO);
    	        
    	        
    	        
    	        layout.setSizeFull();
    	      
    	        add(layout);
    	        
    	        PortfolioCard newPort = context.getBean(PortfolioCard.class);
    	        newPort.addClassName("clickable");
    	        newPort.add(VaadinIcon.CHART_GRID.create());
    	        
    	        layout.add(newPort);
    	        newPort.addClickListener(click -> {
    	        	PortfolioDialog pd = context.getBean(PortfolioDialog.class);
    	        	pd.open();
    	        });
    	       
    	        
    	        ports.forEach(port -> {
    	        	if(port.getLatest()!= null) {
    	        		
    	        		RouterLink link = new RouterLink();
    	        		link.addClassName("portfolio-alert-link");
    	        		link.setRoute(ManagePortfolioView.class);
    	        		
    	        		link.setQueryParameters(QueryParameters.simple(new SingletonMap<>("id",port.getId().toString())));
    	        		
    	        		PortfolioCard pc = context.getBean(PortfolioCard.class,port);
    	        		link.add(pc);
    	   
						/*RouterLink portAlert = new RouterLink();
						portAlert.setRoute(ManagePortfolioView.class);
						portAlert.setQueryParameters(QueryParameters.simple(new SingletonMap<>("id",port.getId().toString())));
						
						portAlert.addClassName("portfolio-alert");
						
						Span portName = new Span(port.getName());
						portName.addClassName("portfolio-name");
						portAlert.add(portName);
						
						Long val = Math.round(port.getLatest().getValue().doubleValue());
						Span portValue = new Span(PortfolioEntryGrid.ReserveFormatWhole.format(val));
						portValue.addClassName("portfolio-value");
						portAlert.add(portValue);
						
						
						Integer warnLevel = PortfolioEntryGrid.getWarnLevel(port.getLatest().getMaxToTriggerPercent());
						//names.add("warn-"+warnLevel);
						Long perc = Math.round(port.getLatest().getMaxToTriggerPercent().doubleValue()*100);
						Span portPerc = new Span(perc.toString());
						portPerc.addClassName("alert-percent");
						portAlert.add(portPerc);
						portAlert.addClassName("warn-"+warnLevel);*/
    	        		
    	        		
    	        		
    	        		
    	        		
    	        		layout.add(link);
    	        		
    	        		
						/*RadialBarChartExample ex = new RadialBarChartExample(port);
						layout.add(ex);*/
    	        		
    	        		
						/*// Creating a chart display area.
						SOChart soChart = new SOChart() {
						
							@Override
							protected String customizeJSON(String json) throws Exception {
								LOGGER.info("customizejson: {}",json);
								return super.customizeJSON(json);
							}
						
							@Override
							protected String customizeDataJSON(String json, AbstractDataProvider<?> data)throws Exception {
								LOGGER.info("customizeDataJSON: {}",json);
								return super.customizeDataJSON(json, data);
							}
							
							
							
						};
						soChart.setSize("300px", "250px");
						
						// Let us define some inline data.
						//CategoryData labels = new CategoryData("Banana", "Apple", "Orange", "Grapes");
						//Data data = new Data(25, 40, 20, 30);
						
						// We are going to create a couple of charts. So, each chart should be positioned
						// appropriately.
						// Create a self-positioning chart.
						//NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
						Position p = new Position();
						p.setTop(Size.percentage(50));
						//nc.setPosition(p); // Position it leaving 50% space at the top
						
						// Second chart to add.
						//BarChart bc = new BarChart(labels, data);
						//RectangularCoordinate rc;
						//rc  = new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
						p = new Position();
						p.setBottom(Size.percentage(55));
						//rc.setPosition(p); // Position it leaving 55% space at the bottom
						//bc.plotOn(rc); // Bar chart needs to be plotted on a coordinate system
						
						GaugeChart gc = new GaugeChart();
						
						gc.setPosition(p);
						
						//gc.setStartAngle(0);
						//gc.setEndAngle(90);
						gc.setMin(10);
						gc.setMax(80);
						gc.setValue(port.getLatest().getMaxToTriggerPercent().doubleValue()*100);
						//gc.setValue(30, 1);
						soChart.add( gc);
						soChart.disableDefaultLegend();
						
						// Now, add the chart display (which is a Vaadin Component) to your layout.
						layout.add(soChart);*/
							
    	        		
    	        		
    	        	}
    	        });
    	    
    

    }
    
    @SuppressWarnings("serial")
	@Component
    @Scope("prototype")
    public static class PortfolioCard extends Span {
    	
    	Portfolio port;
    	
    	public PortfolioCard() {}
    	
    	public PortfolioCard(Portfolio p) {
    		this.port = p;
    	}
    	
    	@PostConstruct
    	public void postConstruct() {
    		
    		Span portAlert = this;
			/*portAlert.setRoute(ManagePortfolioView.class);
			if(port!=null)
				portAlert.setQueryParameters(QueryParameters.simple(new SingletonMap<>("id",port.getId().toString())));*/
    		
    		portAlert.addClassName("portfolio-alert");
    		
    		String name = port!=null?port.getName():"New Portfolio";
    		Span portName = new Span(name);
    		portName.addClassName("portfolio-name");
    		portAlert.add(portName);
    		
    		if(port != null) {
    			
    			PortfolioHistory history = port.getLatest();
    			LOGGER.info("Latest History: {}",history);
    			
	    		Long val = Math.round(history.getValue().doubleValue());
	    		Span portValue = new Span(PortfolioEntryGrid.ReserveFormatWhole.format(val));
	    		portValue.addClassName("portfolio-value");
	    		portAlert.add(portValue);
	    		
	    		
	    		Integer warnLevel = PortfolioEntryGrid.getWarnLevel(history.getMaxToTriggerPercent());
				//names.add("warn-"+warnLevel);
	    		Long perc = Math.round(history.getMaxToTriggerPercent().doubleValue()*100);
	    		Span portPerc = new Span(perc.toString());
	    		portPerc.addClassName("alert-percent");
	    		portAlert.add(portPerc);
	    		portAlert.addClassName("warn-"+warnLevel);
    		}
    		
    	}
    	
    }
    
    public class RadialBarChartExample extends Div {
        public RadialBarChartExample(Portfolio p) {
        	LOGGER.info("port: {} - {}",p,p.getLatest().getMaxToTriggerPercent());
        	double t = Math.round(p.getLatest().getMaxToTriggerPercent().doubleValue()*100);
            ApexCharts radialBarChart = ApexChartsBuilder.get()
                    .withChart(ChartBuilder.get()
                            .withType(Type.radialBar)
                            .build())
                    .withPlotOptions(PlotOptionsBuilder.get()
                    		
                            .withRadialBar(RadialBarBuilder.get()
                            		.withStartAngle(-120d)
                            		.withEndAngle(120d)
                                    .withHollow(HollowBuilder.get()
                                            .withSize("70%")
                                            .build())
                                    .build())
                            .build())
                    .withFill(FillBuilder.get()
                            .withType("gradient")
                            .withGradient(GradientBuilder.get()
                                    .withShade("dark")
                                    .withType("horizontal")
                                    .withShadeIntensity(0.5)
                                    .withGradientToColors("#ff0000","#00ff00","#0000ff")
                                    .withShadeIntensity(.75)
                                    
                                    //.withInverseColors(true)
                                    //.withOpacityFrom(1.0)
                                    //.withOpacityTo(1.0)
                                    .withStops(0.0, 100.0)
                                    .build())
                            .build())
                    .withSeries(t)
                    .withLabels(p.getName())
                    .build();
            add(radialBarChart);
            setWidth("100%");
        }
    }
    
    
    public class GradientRadialBarChartExample extends Div {
        public GradientRadialBarChartExample() {
            ApexCharts gradientRadialBarChart = ApexChartsBuilder.get()
                    .withChart(ChartBuilder.get()
                            .withType(Type.radialBar)
                            .withToolbar(ToolbarBuilder.get().withShow(true).build())
                            .build())
                    .withPlotOptions(PlotOptionsBuilder.get().withRadialBar(RadialBarBuilder.get()
                            .withStartAngle(-135.0)
                            .withEndAngle(225.0)
                            .withHollow(HollowBuilder.get()
                                    .withMargin(0.0)
                                    .withSize("70%")
                                    .withBackground("#fff")
                                    .withPosition(HollowPosition.front)
                                    .withDropShadow(DropShadowBuilder.get()
                                            .withEnabled(true)
                                            .withTop(3.0)
                                            .withBlur(4.0)
                                            .withOpacity(0.24)
                                            .build())
                                    .build())
                            .withTrack(TrackBuilder.get()
                                    .withBackground("#fff")
                                    .withStrokeWidth("67%")
                                    .withDropShadow(DropShadowBuilder.get()
                                            .withTop(-3.0)
                                            .withLeft(0.0)
                                            .withBlur(4.0)
                                            .withOpacity(0.35)
                                            .build())
                                    .build())
                            .withDataLabels(RadialBarDataLabelsBuilder.get()
                                    .withShow(true)
                                    .withName(NameBuilder.get()
                                            .withOffsetY(-10.0)
                                            .withShow(true)
                                            .withColor("#888")
                                            .withFontSize("17px")
                                            .build())
                                    .withValue(ValueBuilder
                                            .get()
                                            .withColor("#111")
                                            .withFontSize("36px")
                                            .withShow(true)
                                            .build())
                                    .build())
                            .build())
                            .build())
                    .withFill(FillBuilder.get()
                            .withType("gradient")
                            .withGradient(GradientBuilder.get()
                                    .withShade("dark")
                                    .withType("horizontal")
                                    .withShadeIntensity(0.5)
                                    .withGradientToColors("#ABE5A1")
                                    .withInverseColors(true)
                                    .withOpacityFrom(1.0)
                                    .withOpacityTo(1.0)
                                    .withStops(0.0, 100.0)
                                    .build())
                            .build())
                    .withSeries(75.0)
                    .withStroke(StrokeBuilder.get()
                            .withLineCap(LineCap.round)
                            .build())
                    .withLabels("Percent")
                    .build();
            add(gradientRadialBarChart);
            setWidth("100%");
        }
    }


}
