package Analytics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.quartz.SchedulerException;

import Messaging.EventManager;
import Scheduling.ScheduleManager;

public class SignalGeneratorTest {

    private SignalGenerator signalGenerator;

    public SignalGeneratorTest() throws SchedulerException {
        EventManager em = new EventManager();
        ScheduleManager sm = new ScheduleManager(em);
        CrossOverManager cm = new CrossOverManager(1, 2, 0.5);
        AnalyticsManager am = new AnalyticsManager(em, sm);
        am.addListener(cm);
        signalGenerator = new SignalGenerator(cm);
    }

    @Test
    public void generateSignalTest() {
        signalGenerator.getSma1().setRecentPrice(0, Double.valueOf(1));
        signalGenerator.generateSignal(0.5);
        assertEquals(signalGenerator.getCurrentPosition(), 0);
        signalGenerator.getSma2().setRecentPrice(0, Double.valueOf(2));
        signalGenerator.getSma2().setRecentPrice(1, Double.valueOf(4));
        signalGenerator.generateSignal(0.5);
        assertEquals(signalGenerator.getCurrentPosition(), -1);
        signalGenerator.getSma1().setRecentPrice(0, Double.valueOf(5));
        signalGenerator.generateSignal(0.5);
        assertEquals(signalGenerator.getCurrentPosition(), 1);
    }
}
