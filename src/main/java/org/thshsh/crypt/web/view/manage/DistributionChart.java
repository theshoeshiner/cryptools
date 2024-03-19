package org.thshsh.crypt.web.view.manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thshsh.crypt.PortfolioEntryHistory;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.vaadin.flow.component.html.Div;

public class DistributionChart extends Div {
		
		 public DistributionChart(ManagePortfolioView v) {

		    	//List<PortfolioEntry> entries = entriesList.entries;
		    	//List<BigInteger> values = new ArrayList<BigInteger>();
		    	//List<String> labels = new ArrayList<String>();
		    	List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		    	
		    	List<Double> series = new ArrayList<>();
		    	List<String> labels = new ArrayList<>();
		    	List<String> colors = new ArrayList<>();
		    	
		    	List<PortfolioEntryHistory> sorted = new ArrayList<PortfolioEntryHistory>(
		    			v.entriesList!=null?
		    			v.entriesList.getEntries():
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
		    	
		    	ManagePortfolioView.LOGGER.info("colors: {}",colors);
		    	
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
		                		
		                        .withType(Type.PIE)
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