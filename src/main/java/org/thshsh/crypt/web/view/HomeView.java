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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.view.manage.ManagePortfolioView;
import org.thshsh.crypt.web.view.manage.PortfolioEntryGrid;
import org.thshsh.crypt.web.view.portfolio.PortfolioDialog;
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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

@SuppressWarnings("serial")
@Route(value = HomeView.PATH, layout = MainLayout.class)
@PageTitle(HomeView.TITLE)
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
	ImageService imageService;
	
	@Autowired
	PortfolioRepository portRepo;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	PlatformTransactionManager transactionManager;

	TransactionTemplate template;

    public HomeView() {
    	super();
    }


    @PostConstruct
    public void postConstruct() {
    	
    	template = new TransactionTemplate(transactionManager);
    	
    	breadcrumbs.resetBreadcrumbs().addBreadcrumb(TITLE, HomeView.class);
    	
    	template.executeWithoutResult(action -> {
    		
    	

    		List<Portfolio> ports = portRepo.findAllByUser(session.getUser());
    	
   
	        FlexibleGridLayout layout = new FlexibleGridLayout()
	        		
	                .withColumns(RepeatMode.AUTO_FILL, new MinMax(new Length("110px"), new Flex(1)))
	                .withAutoRows(new Length("110px"))

	                .withPadding(true)
	                .withSpacing(true)
	                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
	                .withOverflow(GridLayoutComponent.Overflow.AUTO);
	        
	        layout.addClassName("portfolio-grid");
	        
	        
	        layout.setSizeFull();
	      
	        add(layout);
	        
	        PortfolioCard newPort = context.getBean(PortfolioCard.class);
	        newPort.addClassNames("clickable","portfolio-alert-link");
	        newPort.add(VaadinIcon.CHART_GRID.create());
	        
	        layout.add(newPort);
	        newPort.addClickListener(click -> {
	        	PortfolioDialog pd = context.getBean(PortfolioDialog.class);
	        	pd.open();
	        });
	       
	        
	        ports.forEach(port -> {
	        	if(port.getLatest()!= null) {
	        		
	        		PortfolioCardLink link = context.getBean(PortfolioCardLink.class,port);
	        		
					/*RouterLink link = new RouterLink();
					link.addClassName("portfolio-alert-link");
					link.setRoute(ManagePortfolioView.class);
					
					link.setQueryParameters(QueryParameters.simple(new SingletonMap<>("id",port.getId().toString())));
					
					PortfolioCard pc = context.getBean(PortfolioCard.class,port);
					link.add(pc);*/

	        		
	        		layout.add(link);
	        		

	        		
	        	}
	        });
    	    
    	});
    

    }
    
    @SuppressWarnings("serial")
   	@Component
       @Scope("prototype")
    public static class PortfolioCardLink extends RouterLink {
    
    	@Autowired
    	ApplicationContext context;
    	
    	Portfolio port;
    	
    	public PortfolioCardLink() {}
    	
    	public PortfolioCardLink(Portfolio p) {
    		this.port = p;
    		addClassName("portfolio-alert-link");
    		setRoute(ManagePortfolioView.class);
    		
    		setQueryParameters(QueryParameters.simple(new SingletonMap<>("id",port.getId().toString())));
    		
    		

    	}
    	
    	@PostConstruct
    	public void postConstruct() {
    		
    		if(port != null) {
    			
    			PortfolioHistory history = port.getLatest();
    		
	    		Integer warnLevel = PortfolioEntryGrid.getWarnLevel(history.getMaxToTriggerPercent());
	    		this.addClassName("warn-"+warnLevel);
	    		
	    		PortfolioCard pc = context.getBean(PortfolioCard.class,port);
	    		add(pc);
    		
    		}
    	}
    	
    }
    
    @SuppressWarnings("serial")
	@Component
    @Scope("prototype")
    public static class PortfolioCard extends Span {
    	
    	@Autowired
    	ImageService imageService;
    	
    	Portfolio port;
    	
    	public PortfolioCard() {}
    	
    	public PortfolioCard(Portfolio p) {
    		this.port = p;
    	}
    	
    	@PostConstruct
    	public void postConstruct() {
    		
    		//Span portAlert = this;
			/*portAlert.setRoute(ManagePortfolioView.class);
			if(port!=null)
				portAlert.setQueryParameters(QueryParameters.simple(new SingletonMap<>("id",port.getId().toString())));*/
    		
    		this.addClassName("portfolio-alert");
    		
    		String name = port!=null?port.getName():"New Portfolio";
    		Span portName = new Span(name);
    		portName.addClassName("portfolio-name");
    		this.add(portName);
    		
    		if(port != null) {
    			
    			PortfolioHistory history = port.getLatest();
    			LOGGER.info("Latest History: {}",history);
    			
    			Span top = new Span();
    			top.addClassName("top-entries");
    			this.add(top);
    			
    			history.getTopEntries(4).forEach(entry -> {
    				String imageUrl = imageService.getImageUrl(entry.getCurrency());
    				if(imageUrl != null) {
	    				Image image = new Image(imageUrl,"Icon");
	    				image.setWidth(ManagePortfolioView.ICON_SIZE_SMALL);
	    				image.setHeight(ManagePortfolioView.ICON_SIZE_SMALL);
	    				top.add(image);
    				}
    			});
    			
	    		Long val = Math.round(history.getValue().doubleValue());
	    		Span portValue = new Span(PortfolioEntryGrid.ReserveFormatWhole.format(val));
	    		portValue.addClassName("portfolio-value");
	    		this.add(portValue);
	    		
	    		
	    		Integer warnLevel = PortfolioEntryGrid.getWarnLevel(history.getMaxToTriggerPercent());
				//names.add("warn-"+warnLevel);
	    		Long perc = Math.round(history.getMaxToTriggerPercent().doubleValue()*100);
	    		Span portPerc = new Span(perc.toString());
	    		portPerc.addClassName("alert-percent");
	    		this.add(portPerc);
	    		this.addClassName("warn-"+warnLevel);
	    		
	    		
    		}
    		
    	}
    	
    }
    
    public class RadialBarChartExample extends Div {
        public RadialBarChartExample(Portfolio p) {
        	LOGGER.info("port: {} - {}",p,p.getLatest().getMaxToTriggerPercent());
        	double t = Math.round(p.getLatest().getMaxToTriggerPercent().doubleValue()*100);
            ApexCharts radialBarChart = ApexChartsBuilder.get()
                    .withChart(ChartBuilder.get()
                            .withType(Type.RADIALBAR)
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
                            .withType(Type.RADIALBAR)
                            .withToolbar(ToolbarBuilder.get().withShow(true).build())
                            .build())
                    .withPlotOptions(PlotOptionsBuilder.get().withRadialBar(RadialBarBuilder.get()
                            .withStartAngle(-135.0)
                            .withEndAngle(225.0)
                            .withHollow(HollowBuilder.get()
                                    .withMargin(0.0)
                                    .withSize("70%")
                                    .withBackground("#fff")
                                    .withPosition(HollowPosition.FRONT)
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
                            .withLineCap(LineCap.ROUND)
                            .build())
                    .withLabels("Percent")
                    .build();
            add(gradientRadialBarChart);
            setWidth("100%");
        }
    }


}
