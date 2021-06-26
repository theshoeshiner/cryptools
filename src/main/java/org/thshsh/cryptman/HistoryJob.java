package org.thshsh.cryptman;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

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
import org.springframework.transaction.support.TransactionTemplate;

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

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		LOGGER.info("Running Job");

		template = new TransactionTemplate(transactionManager);

		List<Portfolio> ports = portRepo.findAll();

		LOGGER.info("ports: {}",ports);

		ports.forEach(p -> {
			runForPortfolio(p);
		});

	}

	protected void runForPortfolio(Portfolio p) {

		//MutableObject<PortfolioSummary> sum = new MutableObject<>();

		PortfolioHistory history = template.execute(action -> {
			Portfolio port = portRepo.findById(p.getId()).get();

			PortfolioSummary summary = manageService.getSummary(port);
			PortfolioHistory ps = new PortfolioHistory(port, summary);
			ps.setValue(ps.getValue());
			summary.getEntries().forEach(entry -> {
				PortfolioEntryHistory hist = new PortfolioEntryHistory(ps, entry);
				ps.addEntry(hist);
			});

			ps.setValue(summary.getTotalValue());

			portHistRepo.save(ps);

			return ps;
		});

		if(history.getMaxToTriggerPercent().compareTo(alertThreshold) > 0) {
			//fire alert

			String subject = "Portfolio Imbalance Alert: "+format.format(history.getMaxToTriggerPercent().doubleValue());
			String text = "Total Imbalance: "+format.format(history.getTotalImbalance().doubleValue());
			SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("cryptools@thshsh.org");
	        message.setTo("dcwatson84@gmail.com");
	        message.setSubject(subject);
	        message.setText(text);
	        mailSender.send(message);

		}

	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {

	}

}
