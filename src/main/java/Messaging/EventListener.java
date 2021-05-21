package Messaging;

import Scheduling.ScheduleEvent;
import Source.OrderBook;

public interface EventListener {

    /**
     * Handles orderbook events in the orderBookBroker.
     */
    void handleEvent(OrderBook orderBook) throws InterruptedException;

    /**
     * Handles schedule events in the scheduleBroker.
     */
    void handleEvent(ScheduleEvent timer) throws InterruptedException;
}
