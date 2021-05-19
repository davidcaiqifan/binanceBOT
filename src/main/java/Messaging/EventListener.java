package Messaging;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
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
