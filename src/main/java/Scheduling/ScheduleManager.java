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
    
    public void periodicCallBack(int intervalMillis, String tag) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(intervalMillis)
                        .repeatForever())
                .build();
        
        JobDetail timer = JobBuilder.newJob(Timer.class)
                .build();
        timer.getJobDataMap().put("em", em);
        timer.getJobDataMap().put("tag", tag);
        scheduler.scheduleJob(timer, trigger);
        scheduler.start();
    }
    
    //periodic callback should be called by signal generator (call twice for 5 and 10 seconds)
    
//    @Override
//    public void run() {
//        try {
//            periodicCallBack(1000, "SMA5");
//        } catch (SchedulerException e) {
//            e.printStackTrace();   
//        }
//    }
}
