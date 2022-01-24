package org.thshsh.crypt.web.view;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.tax.Asset;
import org.thshsh.crypt.tax.CryptoReport;
import org.thshsh.crypt.tax.Record;
import org.thshsh.crypt.tax.SaleAggregate;
import org.thshsh.crypt.tax.SellRecord;
import org.thshsh.crypt.tax.Transaction;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.BasicTabSheet;
import org.thshsh.vaadin.FunctionUtils;
import org.thshsh.vaadin.ZonedDateTimeRenderer;
import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@SuppressWarnings("serial")
@Route(value = "tax", layout = MainLayout.class)
@PageTitle("Tax Report")
@SecuredByFeatureAccess(feature=Feature.System,access=Access.Super)
public class TaxReportView extends VerticalLayout {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(TaxReportView.class);
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	AsyncTaskExecutor executor;
	
	Grid<Record> recordGrid;
	
	Grid<SaleAggregate> taxGrid;
	
	public TaxReportView() {
		super();
	}
	
	@PostConstruct
	public void postConstruct() {
		
		this.setHeightFull();
		
		Button taxReport = new Button("Tax Report", VaadinIcon.MONEY.create(), click -> {
			UI ui = UI.getCurrent();
			executor.submit(() -> {
				try {
					//template.executeWithoutResult(action -> {
					CryptoReport report = context.getBean(CryptoReport.class);
					report.run();
					
					ui.access(() -> {
						
		
						recordGrid.setItems(report.getProcessor().getAccounts().getRecords());
						
						taxGrid.setItems(report.getProcessor().getAccounts().getTaxableRecords());
					
					});
					
					//});
				}
				catch(Exception e) {
					LOGGER.error("Error",e);
				}
			});
		});
		add(taxReport);
		
		BasicTabSheet tab = new BasicTabSheet();
		tab.setHeightFull();
		add(tab);
		
		recordGrid = new Grid<Record>();
		recordGrid.setHeightFull();
		
		recordGrid.addSelectionListener(select -> {
			recordGrid.getSelectedItems().forEach(record -> {
				Transaction t = record.getTransaction();
				LOGGER.info("t: {}",t);
				LOGGER.info("fromRate: {}",t.getFromRate());
				LOGGER.info("toRate: {}",t.getToRate());
			});
		});
		
		recordGrid
		.addColumn(Record::getId)
		.setHeader("Id")
		.setFlexGrow(0)
		.setWidth("125px")
		.setKey("id")
		;
		
		recordGrid
		.addColumn(FunctionUtils.nestedValue(Record::getTransaction, Transaction::getExternalId))
		.setHeader("Ext Id")
		.setFlexGrow(0)
		.setWidth("200px")
		.setKey("transaction.externalId")
		;
		
		recordGrid
		.addColumn(FunctionUtils.nestedValue(Record::getTransaction, Transaction::getExchange))
		.setHeader("Exchange")
		.setFlexGrow(0)
		.setWidth("125px")
		.setKey("transaction.exchange")
		;
		
		recordGrid
		.addColumn(new ZonedDateTimeRenderer<>(Record::getTimestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
		.setHeader("Timestamp")
		.setFlexGrow(0)
		.setWidth("200px")
		.setKey("timestamp")
		;
		
		recordGrid
		.addColumn(FunctionUtils.nestedValue(Record::getAsset, Asset::getName))
		.setHeader("Asset")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("asset.name")
		;
		
		
		recordGrid
		.addColumn(Record::getType)
		.setHeader("Type")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("type")
		;
		
		recordGrid
		//.addColumn(Record::getBalance)
		.addColumn(new NumberRenderer<>(Record::getQuantity, new DecimalFormat("0.##########")))
		.setHeader("Quantity")
		.setFlexGrow(0)
		.setWidth("150px")
		.setKey("quantity")
		;
		
		recordGrid
		//.addColumn(Record::getBalance)
		.addColumn(new NumberRenderer<>(Record::getPrice, new DecimalFormat("$0.00")))
		.setHeader("Price")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("price")
		;
		
		recordGrid
		//.addColumn(Record::getBalance)
		.addColumn(new NumberRenderer<>(Record::getPricePer, new DecimalFormat("$0.00")))
		.setHeader("Price Per")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("pricePer")
		;
		
		recordGrid
		//.addColumn(Record::getBalance)
		.addColumn(new NumberRenderer<>(Record::getBalance, new DecimalFormat("0.##########")))
		.setHeader("Balance")
		.setFlexGrow(0)
		.setWidth("200px")
		;
		
		recordGrid
		//.addColumn(Record::getBalance)
		.addColumn(record -> {
			if(record instanceof SellRecord) {
						/*SellRecord sell = (SellRecord) record;
						sell.getSaleRecords().forEach(sale -> {
							
						});*/
			}
			return "";
		})
		.setHeader("Balance")
		.setFlexGrow(0)
		.setWidth("200px")
		;
		
		
		Anchor a = new Anchor(new StreamResource("report.csv", Exporter.exportAsCSV(recordGrid)), "Download As CSV");
		add(a);
		
		//add(recordGrid);
		tab.addTab("Records", recordGrid);

		
		taxGrid = new Grid<>();
		taxGrid.setHeightFull();
		tab.addTab("Taxables", taxGrid);
		
		taxGrid
		.addColumn(SaleAggregate::getId)
		.setHeader("Id")
		.setFlexGrow(0)
		.setWidth("200px")
		.setKey("id")
		;
		

		taxGrid
		.addColumn(new ZonedDateTimeRenderer<>(FunctionUtils.nestedValue(SaleAggregate::getSellRecord,Record::getTimestamp), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
		.setHeader("Timestamp")
		.setFlexGrow(0)
		.setWidth("200px")
		.setKey("timestamp")
		;
		
		taxGrid
		.addColumn(FunctionUtils.nestedValue(SaleAggregate::getSellRecord,Record::getAsset, Asset::getName))
		.setHeader("Asset")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("sellRecord.asset.name")
		;
		
		taxGrid
		.addColumn(new NumberRenderer<>(SaleAggregate::getBasis, new DecimalFormat("$0.00")))
		.setHeader("Basis")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("basis")
		;
		
		taxGrid
		.addColumn(new NumberRenderer<>(SaleAggregate::getProceeds, new DecimalFormat("$0.00")))
		.setHeader("Proceeds")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("proceeds")
		;
		
		taxGrid
		.addColumn(new NumberRenderer<>(SaleAggregate::getGain, new DecimalFormat("$0.00")))
		.setHeader("Gains")
		.setFlexGrow(0)
		.setWidth("100px")
		.setKey("gains")
		;
		
	}
	
	/*public void run() {
		
		CryptoReport report = context.getBean(CryptoReport.class);
		report.run();
	}*/

}
