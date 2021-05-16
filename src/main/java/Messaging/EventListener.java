package Messaging;

import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;

public interface EventListener {

    void handleEvent(OrderBook orderBook);

    void handleEvent(AggTrade aggTrade);
}
