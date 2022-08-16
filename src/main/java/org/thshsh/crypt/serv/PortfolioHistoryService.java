package org.thshsh.crypt.serv;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.job.HistoryJob;

@Service
public class PortfolioHistoryService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PortfolioHistoryService.class);
	
	@Autowired
	Scheduler scheduler;

	public void runHistoryJob() {
		runHistoryJob(null);
	}
	
	public void runHistoryJob(Portfolio entity) {
		
		LOGGER.info("runHistoryJob: {}",entity);
		
		try {
			JobDetail jd = scheduler.getJobDetail(JobKey.jobKey("history-job"));
			Trigger t = TriggerBuilder.newTrigger().forJob("history-job").startNow().build();
			if(entity != null) {
				t.getJobDataMap().put(HistoryJob.PORTFOLIO_ID_PROP, entity.getId().toString());
				t.getJobDataMap().put(HistoryJob.FORCE_PROP,true);
			}
			//t.getJobDataMap().put(HistoryJob.FORCE_PROP, true);
			scheduler.scheduleJob(t);
		}
		catch (SchedulerException e) {
			throw new IllegalStateException(e);
		}
	}

}
