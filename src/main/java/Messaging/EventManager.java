package Messaging;

import com.binance.api.client.domain.event.AggTradeEvent;

import Scheduling.ScheduleEvent;

/**
 * EventManager class manages orderbook, aggtrade, and schedule events.
 */
public class EventManager {
    private EventBroker<AggTradeEvent> aggTradesBroker = new EventBroker<>();
    private EventBroker<Source.OrderBook> orderBookBroker = new EventBroker<>();
    private EventBroker<ScheduleEvent> scheduleBroker = new EventBroker<>();

    /**
     * Publishes orderbook events to the queue in orderBookBroker.
     */
    public void publish(Source.OrderBook orderbook) throws InterruptedException {
        orderBookBroker.addEvent(orderbook);
    }

    /**
     * Publishes trade events to the queue in aggTradesBroker.
     */
    public void publish(AggTradeEvent aggTradeEvent) throws InterruptedException {
        aggTradesBroker.addEvent(aggTradeEvent);
    }

    /**
     * Publishes schedule events to the queue in scheduleBroker.
     */
    public void publish(ScheduleEvent timer) throws InterruptedException {
        scheduleBroker.addEvent(timer);
    }

    /**
     * @return the orderBookBroker.
     */
    public EventBroker getOrderBookBroker() {
        return orderBookBroker;
    }

    /**
     * @return the scheduleBroker.
     */
    public EventBroker getScheduleBroker() {
        return scheduleBroker;
    }

    /**
     * @return the aggTradesBroker.
     */
    public EventBroker getAggTradesBroker() {
        return aggTradesBroker;
    }
}
