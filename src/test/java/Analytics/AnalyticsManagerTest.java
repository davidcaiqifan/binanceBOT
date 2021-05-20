package Analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.TreeMap;

import org.junit.Test;
import org.quartz.SchedulerException;
import Messaging.EventManager;
import Scheduling.ScheduleEvent;
import Scheduling.ScheduleManager;
import Source.OrderBook;
import Source.OrderBookBuilder;

public class AnalyticsManagerTest {
    
    @Test
    public void initializeTest() throws SchedulerException, InterruptedException {
        EventManager em = new EventManager();
        ScheduleManager sm = new ScheduleManager(em);
        AnalyticManager am = new AnalyticManager(5, 10, em, sm);
        am.initialize();
        Thread.sleep(1000);
        ScheduleEvent scheduleEvent1 = (ScheduleEvent) em.getScheduleBroker().getEventQueue().remove();
        assertTrue(scheduleEvent1.getTag().equals("sma2") || scheduleEvent1.getTag().equals("sma1"));
        sm.getScheduler().shutdown();
    }
    
    @Test
    public void handleOrderBookEventTest() throws SchedulerException, InterruptedException {
        EventManager em = new EventManager();
        ScheduleManager sm = new ScheduleManager(em);
        AnalyticManager am = new AnalyticManager(5, 10, em, sm);
        OrderBook orderBook = new OrderBookBuilder().withAsk(new BigDecimal(0.5), new BigDecimal(1000)).build();
        am.handleEvent(orderBook);
        assertEquals(am.getOrderBookCache().lastEntry().getValue(), orderBook);
    }
}
