package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioEntryHistory;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.LegendBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.config.xaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ValueChart extends Div {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ValueChart.class);
	
	@Autowired
	PortfolioHistoryRepository histRepo;
	
	@Autowired
	PlatformTransactionManager transactionManager;

	TransactionTemplate template;
	
	protected long totalpoints = 500l;
	//long days = 60;
	protected Duration subtract = Duration.ofDays(90);
	protected ApexCharts chart;
	protected Button selected;
	
	protected Portfolio entity;
	
    public ValueChart(Portfolio e) {

    	this.entity = e;
    	
    	//long days = 60l;
    	//long minutes = TimeUnit.DAYS.convert(days, TimeUnit.MINUTES);
    	
        setWidth("100%");
        setHeight("600px");
        
       HorizontalLayout buttons = new HorizontalLayout();
       buttons.setWidthFull();
       buttons.setAlignItems(Alignment.CENTER);
       buttons.setJustifyContentMode(JustifyContentMode.CENTER);
       add(buttons);
       
       buttons.add(button("1w",Duration.ofDays(7),false));
       buttons.add(button("1m",Duration.ofDays(30),false));
       buttons.add(button("3m",Duration.ofDays(90),true));
       buttons.add(button("6m",Duration.ofDays(180),false));
       buttons.add(button("1y",Duration.ofDays(365),false));
       
     

    }
    
    @PostConstruct
    public void postConstruct() {
    	
    	template = new TransactionTemplate(transactionManager);
    	
    	renderChart();
    }
    
    protected Button button(String name, Duration dur, boolean prim) {
    	Button b = new Button(name);
	    b.addClickListener(click -> {
	    	subtract = dur;
	    	b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    	if(selected != null) selected.removeThemeVariants(ButtonVariant.LUMO_PRIMARY); 
	    	selected = b;
	    	renderChart();
	    });
	    b.addThemeVariants(ButtonVariant.LUMO_SMALL);
	    if(prim) {
	    	b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    	selected = b;
	    }
	    return b;
    }
    
    public void renderChart() {
    	
    	template.executeWithoutResult(action -> {

	    	if(chart != null) this.remove(chart);
	    	
	    	
	    	
	    	long minutes = subtract.toMinutes();
	    	LOGGER.info("minutes: {}",minutes);
	    	long pointsEveryMinute = minutes/totalpoints;
	    	LOGGER.info("points are: {} min apart",pointsEveryMinute);
	    	//long days = 30;
	    	//long valueEveryHours = 30*24
	    	ZonedDateTime zdt = ZonedDateTime.now().minus(subtract);
	    	LOGGER.info("start time: {}",zdt);
	    	
	    	if(entity.getLatest()!=null) {
	    	
		    	//PortfolioHistory latest = histRepo.getById(entity.getLatest().getId());
		    	

		    	//TODO
				/*Map<Currency,List<BigInteger>> currencyValueMap = new HashMap<>();
				latest.getEntries().forEach(entry -> {
					currencyValueMap.put(entry.getCurrency(), new ArrayList<>());
				});*/
		    	
		    	
				List<PortfolioHistory> ph = histRepo.findByPortfolioAndTimestampGreaterThanOrderByTimestampAsc(entity, zdt);
				LOGGER.info("history: {}",ph.size());
				LOGGER.info("history: {}",ph.get(0).getTimestamp());
				
		   
				
		    	List<String> dates = new ArrayList<>();
				List<BigInteger> valuePerHour = new ArrayList<>();
				List<BigDecimal> thresh = new ArrayList<>();
				MutableObject<ZonedDateTime> last = new MutableObject<>();
				ph.forEach(hist -> {
					if(hist.getValue()!=null) {
						if(last.getValue()==null || last.getValue().plusMinutes(pointsEveryMinute).isBefore(hist.getTimestamp())) {
							valuePerHour.add(hist.getValue().toBigInteger());
							thresh.add(hist.getMaxToTriggerPercent());
							dates.add(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(hist.getTimestamp().withZoneSameInstant(ZoneId.systemDefault())));
							LOGGER.debug("adding: {}",hist);
							last.setValue(hist.getTimestamp());
							
						
							//TODO
							/*currencyValueMap.keySet().forEach(curr -> {
								PortfolioEntryHistory entryHist = hist.getEntry(curr).orElse(null);
								currencyValueMap.get(curr).add(entryHist==null?BigInteger.ZERO:entryHist.getValue().toBigInteger());
							});*/
						
						}
					}
				});
		
				LOGGER.debug("valuePerHour: {}",valuePerHour);
				LOGGER.debug("dates: {}",dates);
		
				//List<BigInteger> values = valuePerHour;
				//List<String> labels = dates;
		
				LOGGER.debug("point count: {}",valuePerHour.size());
				//LOGGER.debug("currency values: {}",currencyValues.size());
				//LOGGER.debug("currency values: {}",currencyValues);
		
				List<String> colors = new ArrayList<>();
				List<Series<?>> series = new ArrayList<>();
				series.add(new Series<>("USD Value", valuePerHour.toArray()));
				colors.add("var(--money-green)");
				
				//TODO
				/*currencyValueMap.forEach((c,l) -> {
					LOGGER.info("adding {}",c);
					LOGGER.info("data {}",l);
					series.add(new Series<>(c.getKey(),l.toArray()));
					colors.add("#"+c.getColorHex());
				});*/
		
				LOGGER.info("colors: {}",colors);
				
		        chart = ApexChartsBuilder.get()
		
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
		                		.withCurve(Curve.smooth)
		                		.withColors(colors.toArray(new String[0]))
		                		.withWidth(3d)
		                		.build()
		                		)
		               //.withFill(FillBuilder.get().withOpacity(0d).build())
		                .withSeries(
		                		series.toArray(new Series[0])
		                		//new Series<>("USD Value", valuePerHour.toArray())
		                		//,new Series<>(curr.getKey(), currencyValues.toArray())
		                		
		                		
		                //.withSeries(new Series<>("USD Value", valuePerHour.toArray())
		                //new Series<>("Alert Threshold", thresh.toArray())
		                )
		                
		               .withLabels(dates.toArray(new String[dates.size()]))
		
		                .withXaxis(XAxisBuilder.get()
		                        .withType(XAxisType.datetime)
		                        .withLabels(LabelsBuilder
		                        		.get()
		                        		.withFormat("MMM dd")
		                        		.build())
		                        .build())
		                .withYaxis(
		                		YAxisBuilder.get()
			                		.withDecimalsInFloat(0d)
			                		//.withTickAmount(10d)
			                        .withMin(0d)
			                        .build()
						/* ,YAxisBuilder.get()
						 	.withOpposite(true)
						 	.withMax(1)
						 	.withMin(0)
						 	.withDecimalsInFloat(2d)
						 	.build()*/
		                        )
		                .withLegend(
		                		LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left)
		                		
		                		.build()
		                		)
		                .build();
		        add(chart);
		        chart.setHeight("600px");
		        
	    	}
        
    	});
        
    }
}