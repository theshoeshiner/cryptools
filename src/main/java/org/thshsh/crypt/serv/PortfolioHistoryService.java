package org.thshsh.crypt.serv;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thshsh.cryptman.HistoryJob;
import org.thshsh.cryptman.Portfolio;

@Service
public class PortfolioHistoryService {
	
	@Autowired
	Scheduler scheduler;
	
	public void runHistoryJob(Portfolio entity) {
		try {
			scheduler.getJobDetail(JobKey.jobKey(""));
			Trigger t = TriggerBuilder.newTrigger().forJob("history-job").startNow().build();
			t.getJobDataMap().put(HistoryJob.PORTFOLIO_ID_PROP, entity.getId().toString());
			scheduler.scheduleJob(t);
		}
		catch (SchedulerException e) {
			throw new IllegalStateException(e);
		}
	}

}
