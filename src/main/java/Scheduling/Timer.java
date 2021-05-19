package Scheduling;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import Messaging.EventManager;

public class Timer implements Job {

    /**
     * Publishes a Schedule Event to the ScheduleBroker queue.
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        EventManager em = (EventManager) arg0.getJobDetail().getJobDataMap().get("em");
        String tag = (String) arg0.getJobDetail().getJobDataMap().get("tag");
        try {
            em.publish(new ScheduleEvent(tag));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
