package Messaging;

import static org.junit.Assert.assertEquals;

import java.util.TreeMap;

import org.junit.Test;
import com.binance.api.client.domain.event.AggTradeEvent;
import Scheduling.ScheduleEvent;
import Source.OrderBook;

public class EventManagerTest {
    private EventManager eventManager = new EventManager();
    
    @Test
    public void publish_orderBook() throws InterruptedException {
        OrderBook orderBook = new OrderBook();
        orderBook.getOrderBookCache().put("ASKS", new TreeMap<>());
        eventManager.publish(orderBook);
        assertEquals(eventManager.getOrderBookBroker().getEventQueue().poll(), orderBook);
    }

    @Test
    public void publish_aggTradeEvent() throws InterruptedException {
        AggTradeEvent aggTradeEvent = new AggTradeEvent();
        aggTradeEvent.setEventType("Trade");
        eventManager.publish(aggTradeEvent);
        assertEquals(eventManager.getAggTradesBroker().getEventQueue().poll(), aggTradeEvent);
    }

    @Test
    public void publish_scheduleEvent() throws InterruptedException {
        ScheduleEvent scheduleEvent = new ScheduleEvent("Test");
        eventManager.publish(scheduleEvent);
        assertEquals(eventManager.getScheduleBroker().getEventQueue().poll(), scheduleEvent);
    }
}
