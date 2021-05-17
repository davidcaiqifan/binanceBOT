package Messaging;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import Scheduling.ScheduleEvent;
import Source.OrderBook;

public interface EventListener {

    void handleEvent(OrderBook orderBook);

    void handleEvent(AggTradeEvent aggTrade);

    void handleEvent(ScheduleEvent timer) throws InterruptedException;
}
