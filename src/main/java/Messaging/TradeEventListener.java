package Messaging;

import Scheduling.ScheduleEvent;
import Source.AggsTradeCache;
import com.binance.api.client.domain.event.AggTradeEvent;

public interface TradeEventListener {
    /**
     * Handles trade events in the aggTradesBroker.
     */
    void handleEvent(AggTradeEvent aggTradeEvent) throws InterruptedException;

    /**
     * Handles schedule events in the scheduleBroker.
     */
    void handleEvent(ScheduleEvent timer) throws InterruptedException;
}
