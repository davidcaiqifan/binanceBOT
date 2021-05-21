package Scheduling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScheduleEventTest {

    @Test
    public void getTagTest() {
        ScheduleEvent scheduleEvent = new ScheduleEvent("Tag");
        assertEquals("Tag", scheduleEvent.getTag());
    }
}
