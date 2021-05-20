package Scheduling;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import Messaging.EventManager;

public class ScheduleManagerTest {
    
    @Test
    public void periodicCallBackTest() throws SchedulerException {
        EventManager eventManager = new EventManager();
        ScheduleManager scheduleManager = new ScheduleManager(eventManager);
        scheduleManager.periodicCallBack(100, "Test");
        assertTrue(scheduleManager.getScheduler().checkExists(new JobKey("Test (100ms) Job")));
        assertTrue(scheduleManager.getScheduler().checkExists(new TriggerKey("Test (100ms) Trigger")));
        scheduleManager.getScheduler().shutdown();
    }
}
