package Scheduling;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import Messaging.EventManager;

/**
 * ScheduleManager manages ScheduleEvents and executes jobs at given intervals.
 */
public class ScheduleManager {

    private EventManager em;
    private Scheduler scheduler;

    /**
     * Initializes ScheduleManager with EventManager.
     */
    public ScheduleManager(EventManager em) throws SchedulerException {
        this.em = em;
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
    }

    /**
     * Starts the scheduler and executes timer job at intervals of intervalMillis.
     */
    public void periodicCallBack(int intervalMillis, String tag) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(tag + " (" + intervalMillis + "ms) Trigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(intervalMillis)
                        .repeatForever())
                .build();

        JobDetail timer = JobBuilder.newJob(Timer.class)
                .withIdentity(tag + " (" + intervalMillis + "ms) Job")
                .build();
        timer.getJobDataMap().put("em", em);
        timer.getJobDataMap().put("tag", tag);
        scheduler.scheduleJob(timer, trigger);
        scheduler.start();
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
}
