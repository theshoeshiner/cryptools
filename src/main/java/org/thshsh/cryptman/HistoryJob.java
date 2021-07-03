package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.sound.sampled.Port;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.NumberUtils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HistoryJob implements InterruptableJob {

	public static final Logger LOGGER = LoggerFactory.getLogger(HistoryJob.class);

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
	JavaMailSender mailSender;

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

		template = new TransactionTemplate(transactionManager);

		List<Portfolio> ports = portRepo.findAll();

		LOGGER.info("ports: {}",ports);

		ports.forEach((Portfolio p) -> {
			runForPortfolio(p);
		});

	}

	protected void runForPortfolio(Portfolio p) {



		PortfolioHistory history = template.execute( (TransactionStatus action) -> {

			PortfolioHistory mostRecent = portHistRepo.findOneByPortfolioOrderByTimestampDesc(p);

			Portfolio port = portRepo.findById(p.getId()).get();


			PortfolioSummary summary = manageService.getSummary(port);
			PortfolioHistory portHistory = new PortfolioHistory(port, summary);
			portHistory.setValue(portHistory.getValue());
			summary.getEntries().forEach(entry -> {
				PortfolioEntryHistory hist = new PortfolioEntryHistory(portHistory, entry);
				portHistory.addEntry(hist);
			});



			portHistory.setValue(summary.getTotalValue());


			portHistRepo.save(portHistory);

			return portHistory;
		});



		if(history.getMaxToTriggerPercent().compareTo(alertThreshold) > 0) {
			//fire alert

			//PortfolioEntryHistory max = history.getMaxTriggerEntry();

			//String subject = "Portfolio '' Imbalance Alert: "+format.format(history.getMaxToTriggerPercent().doubleValue());
			//String subject = String.format(subjectFormat, p.getName(),(int)(history.getMaxToTriggerPercent().doubleValue()*100));
			String subject = String.format(subjectFormat, p.getName());
			//String text = "Total Imbalance: "+format.format(history.getTotalImbalance().doubleValue()*100);\



			StringBuilder emailText = new StringBuilder();
			emailText.append(String.format(textFormat, NumberUtils.BigDecimalToPercentInt(history.getTotalImbalance())));

			history.getEntries().forEach(entry -> {
				LOGGER.info("entry: {}",entry.getThresholdPercent());
				if(entry.getToTriggerPercent().compareTo(BigDecimal.ONE) > 0) {
					String entryText = String.format(entryFormat, entry.getCurrency().getKey(),NumberUtils.BigDecimalToPercentInt(entry.getToTriggerPercent()));
					emailText.append("\n");
					emailText.append(entryText);
				}
			});

			//String text = ;
			SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("cryptools@thshsh.org");
	        message.setTo("dcwatson84@gmail.com");
	        message.setSubject(subject);
	        message.setText(emailText.toString());
	        mailSender.send(message);

		}

	}



	@Override
	public void interrupt() throws UnableToInterruptJobException {

	}

}
