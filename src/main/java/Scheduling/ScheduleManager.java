package Scheduling;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import Messaging.EventManager;

public class ScheduleManager {
    
    private EventManager em;
    private Scheduler scheduler;
    
    public ScheduleManager(EventManager em) throws SchedulerException {
        this.em = em;
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
    }
    
    public void periodicCallBack(int intervalMillis) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(intervalMillis + "ms Trigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(intervalMillis)
                        .repeatForever())
                .build();
        
        JobDetail timer = JobBuilder.newJob(Timer.class)
                .withIdentity(intervalMillis + "ms Timer")
                .build();
        timer.getJobDataMap().put("em", em);
        scheduler.scheduleJob(timer, trigger);
        scheduler.start();
    }
}
