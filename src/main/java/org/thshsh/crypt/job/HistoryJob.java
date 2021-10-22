package org.thshsh.crypt.job;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioHistoryRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.serv.MailService;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.serv.MarketRateService;
import org.thshsh.crypt.web.AppConfiguration;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HistoryJob implements InterruptableJob {

	public static final Logger LOGGER = LoggerFactory.getLogger(HistoryJob.class);
	
	public static final String PORTFOLIO_ID_PROP = "PORTFOLIO_ID";
	public static final String FORCE_PROP = "FORCE";

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	PortfolioHistoryRepository portHistRepo;

	@Autowired
	MarketRateService rateService;

	@Autowired
	ManagePortfolioService manageService;

	@Autowired
	MailService mailService;
	
	@Autowired
	AppConfiguration appConfig;

	TransactionTemplate template;
	NumberFormat format = new DecimalFormat("###.#%");

	BigDecimal alertThreshold = BigDecimal.ONE;
	//BigDecimal alertThreshold = new BigDecimal(.8d);

	String subjectFormat = "%s Portfolio Imbalance Alert";
	String textFormat = "Total Portfolio Imbalance %d%%";
	String entryFormat = "%s Imbalance: %d%%";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		LOGGER.info("Running Job");
		
		JobDataMap map = context.getMergedJobDataMap();
		
		if(appConfig.getJob().getHistory() || map.containsKey(FORCE_PROP) ) {
			

			Object portId = map.get(PORTFOLIO_ID_PROP);
			
			List<Portfolio> ports;
			
			if(portId == null) {
				ports = portRepo.findAll();
			}
			else {
				ports = Arrays.asList(portRepo.findById(Long.valueOf(portId.toString())).get());
			}
	
			template = new TransactionTemplate(transactionManager);
	
	
			LOGGER.info("ports: {}",ports);
	
			ports.forEach((Portfolio p) -> {
				try {
					runForPortfolio(p);
				}
				catch(Exception e) {
					LOGGER.info("History Job Failed for Portfolio: {}",p,e);
				}
			});
		
		}

	}

	protected void runForPortfolio(Portfolio portfolio) {



		template.executeWithoutResult((TransactionStatus action) -> {

			Portfolio port = portRepo.findById(portfolio.getId()).get();
			PortfolioHistory history = manageService.createHistory(port);
			
			
			if(!port.getSettings().isAlertsDisabled() && history.getMaxToTriggerPercent().compareTo(alertThreshold) > 0) {
				
				mailService.sendPortfolioAlert(port);


			}
			
			
		});



		

	}



	@Override
	public void interrupt() throws UnableToInterruptJobException {

	}

}
