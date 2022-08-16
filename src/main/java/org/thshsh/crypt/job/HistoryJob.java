package org.thshsh.crypt.job;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.mutable.MutableBoolean;
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
import org.thshsh.crypt.PortfolioAlert;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.repo.PortfolioAlertRepository;
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
	
	//public static final Integer[] ALERT_WAIT_HOURS = new Integer[] {1,2,3,5,8,13,21};
	
	//public static final Integer[] ALERT_WAIT = new Integer[] {1,2,3,5,8};

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	PortfolioRepository portRepo;
	
	@Autowired
	PortfolioAlertRepository alertRepo;

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

	Duration onlyRunEvery;
	MutableBoolean force;
	
	@PostConstruct
	public void postConstruct() {
		onlyRunEvery = Duration.ofMinutes(appConfig.getJob().getMinutes());
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		
		
		JobDataMap map = context.getMergedJobDataMap();
		force = new MutableBoolean(map.containsKey(FORCE_PROP));
		if(appConfig.getJob().getHistory() || force.booleanValue() ) {
			
			LOGGER.info("Running Job");
			
			Object portId = map.get(PORTFOLIO_ID_PROP);
			
			List<Portfolio> ports;
			
			if(portId == null) {
				ports = portRepo.findAll();
				//shuffle portfolios so that we're not always hammering the same API key
				ports = new ArrayList<Portfolio>(ports);
				Collections.shuffle(ports);
			}
			else {
				force.setTrue();
				ports = Arrays.asList(portRepo.findById(Long.valueOf(portId.toString())).get());
			}
	
			template = new TransactionTemplate(transactionManager);
	
	
			LOGGER.info("ports: {}",ports);
	
			ports.forEach((Portfolio p) -> {
				
				try {
					
					if(force.booleanValue()) runForPortfolio(p);
					else {
						if(p.getLatest()!=null) {
							LOGGER.info("latest: {}",p.getLatest());
							
							PortfolioHistory latest = p.getLatest();
							ZonedDateTime nextRun = latest.getTimestamp().plus(onlyRunEvery);
							if(nextRun.isBefore(ZonedDateTime.now())) {
								runForPortfolio(p);
							}
							else {
								LOGGER.info("Skipping Portfolio: {} Till Next Run: {}",p,nextRun);
							}
						}
						else runForPortfolio(p);
					}
					
					
					
					//runForPortfolio(p);
				}
				catch(Exception e) {
					LOGGER.info("History Job Failed for Portfolio: {}",p,e);
				}
			});
		
		}

	}

	protected void runForPortfolio(Portfolio p) {

		template.executeWithoutResult((TransactionStatus action) -> {
			
			ZonedDateTime now = ZonedDateTime.now();
			
			LOGGER.info("runForPortfolio: {}",p);

			Portfolio port = portRepo.findById(p.getId()).get();
			PortfolioHistory history = manageService.createHistory(port);
			Boolean muted = false;
			
			//update silent till parameter
			if(port.getSettings().getSilentTill() != null) {
				muted = port.getSettings().getSilentTill().isAfter(now);
				if(!muted) port.getSettings().setSilentTill(null);
			}
			
			//check alerts disabled
			muted = muted || Boolean.TRUE.equals(port.getSettings().getAlertsDisabled());
			
			LOGGER.info("Muted: {}",muted);
			
			if(history.getMaxToTriggerPercent().compareTo(alertThreshold) > 0) {
				
				
				Boolean skip = false;
				Integer repeat = 0;
				
				if(!force.booleanValue()) {
				
				//if we already have an alert then check the repeat interval
					if(port.getLatestAlert() != null) {
						//check repeat wait time
						PortfolioAlert latest = port.getLatestAlert();
						repeat = latest.getRepeat();
						List<Integer> wait = appConfig.getAlertWaitDays();
	
						Integer waitDays = wait.get(repeat < wait.size() ? repeat : wait.size()-1);
						
						ZonedDateTime waitTill = latest.getTimestamp().plusDays(waitDays);
						skip = waitTill.isAfter(now);
						repeat = latest.getRepeat()+1;
					}
					
				}
				
				LOGGER.info("Skip: {}",skip);
				
				if(!skip) {
					
					//send alert and update latest
					PortfolioAlert alert = new PortfolioAlert();
					alert.setPortfolio(port);
					alert.setRepeat(repeat);
					alert.setTimestamp(now);
					alert.setHistory(history);
					alert.setMuted(muted);
					alertRepo.save(alert);
					port.setLatestAlert(alert);
					//always save alert but only send if not muted
					if(!muted) mailService.sendPortfolioAlert(alert);
				}
				else {
					//skip alert for now based on timing
				}
			}
			else {
				//no alert
				port.setLatestAlert(null);
			}
			
			
		});



		

	}

	
	

	@Override
	public void interrupt() throws UnableToInterruptJobException {

	}

}
