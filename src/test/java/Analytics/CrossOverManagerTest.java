package Analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.quartz.SchedulerException;
import Messaging.EventManager;
import Scheduling.ScheduleManager;
import Source.OrderBook;
import Source.OrderBookBuilder;

public class CrossOverManagerTest {
    
//    @Test
//    public void initializeTest() throws SchedulerException, InterruptedException {
//        EventManager em = new EventManager();
//        ScheduleManager sm = new ScheduleManager(em);
//        CrossOverManager am = new CrossOverManager(5, 10, em, sm);
//        am.initialize();
//        Thread.sleep(1000);
//        ScheduleEvent scheduleEvent1 = (ScheduleEvent) em.getScheduleBroker().getEventQueue().remove();
//        assertTrue(scheduleEvent1.getTag().equals("sma2") || scheduleEvent1.getTag().equals("sma1"));
//        sm.getScheduler().shutdown();
//    }
    
    @Test
    public void handleOrderBookEventTest() throws SchedulerException, InterruptedException {
        EventManager em = new EventManager();
        ScheduleManager sm = new ScheduleManager(em);
        CrossOverManager cm = new CrossOverManager(5, 10);
        AnalyticsManager am = new AnalyticsManager(em, sm);
        am.addListener(cm);
        OrderBook orderBook = new OrderBookBuilder().withAsk(new BigDecimal(0.5), new BigDecimal(1000)).build();
        am.handleEvent(orderBook);
        assertEquals(cm.getOrderBookCache().lastEntry().getValue(), orderBook);
    }
}
