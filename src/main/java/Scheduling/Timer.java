package Scheduling;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import Messaging.EventManager;

public class Timer implements Job {
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        EventManager em = (EventManager) arg0.getJobDetail().getJobDataMap().get("em");
        try {
            em.publish(new ScheduleEvent());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
