package org.thshsh.crypt.job;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerConfiguration {

	public static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfiguration.class);

	@Autowired
	Scheduler scheduler;

	@PostConstruct
	public void postConstruct() {

		createHistoryJobAndTrigger();

	}
	
	protected void createHistoryJobAndTrigger() {
		
		LOGGER.info("createHistoryJobAndTrigger");
		
		try {

			JobKey jk = JobKey.jobKey("history-job");
			JobDetail jd;
			//scheduler.checkExists(jobKey)
	
			if(!scheduler.checkExists(jk)) {
				jd = JobBuilder.newJob(HistoryJob.class).storeDurably().withIdentity(jk).build();
				scheduler.addJob(jd, true,true);
				LOGGER.info("adding job: {}",jd);
			}
			else jd = scheduler.getJobDetail(jk);
			
			LOGGER.info("job: {}",jd);

			TriggerKey tk = TriggerKey.triggerKey("history-trigger");
			
			if(scheduler.checkExists(tk)) {
				scheduler.unscheduleJob(tk);
			}
			
			//if(!scheduler.checkExists(tk)) {
				//CronTrigger ct = new CronTrigger("0 0 * * * *", ZoneId.of(ZoneId.SHORT_IDS.get("EST")));

				Trigger trigger = TriggerBuilder.newTrigger()
						.forJob(jk)
		                .withIdentity(tk)
		                
		                //.startAt(DateBuilder.todayAt(startHr, startMin, startSec))
		                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0,30 * * * ?"))
		                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(2))
		                .build();

				LOGGER.info("adding trigger: {}",trigger);
//				scheduler.scheduleJob(jd, trigger);
				scheduler.scheduleJob(trigger);
				//TriggerBuilder.newTrigger()
				//CronTrigger ct = TriggerBuilder.newTrigger().forJob(jk).withIdentity(tk).build();

			//}
			//else {
				
				
				
				//scheduler.resetTriggerFromErrorState(tk);
			//}

		}
		catch(SchedulerException e) {
			LOGGER.error("",e);
		}
	}

}
