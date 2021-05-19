package Scheduling;

/**
 * ScheduleEvent is an event made by the timer job.
 */
public class ScheduleEvent {
    private String tag;

    /**
     * Initializes a ScheduleEvent.
     * @param tag differentiates ScheduleEvents for SMA1 and ScheduleEvents for SMA2.
     */
    public ScheduleEvent(String tag) {
        this.tag = tag;
    }

    /**
     * @return tag for SMA1 or SMA2.
     */
    public String getTag() {
        return tag;
    }
}
