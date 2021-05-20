package Scheduling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
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

public class TimerTest {
    
    @Test
    public void executeTest() throws SchedulerException, InterruptedException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(100)
                        .repeatForever())
                .build();
        
        JobDetail timer = JobBuilder.newJob(Timer.class)
                .build();

        EventManager em = new EventManager();
        timer.getJobDataMap().put("em",  em);
        timer.getJobDataMap().put("tag", "Tag");
        scheduler.scheduleJob(timer, trigger);
        scheduler.start();
        Thread.sleep(1000);
        ScheduleEvent scheduleEvent = (ScheduleEvent) em.getScheduleBroker().getEventQueue().remove();
        assertEquals(scheduleEvent.getTag(), "Tag");
        scheduler.shutdown();
    }
}
