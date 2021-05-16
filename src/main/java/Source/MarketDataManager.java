package Source;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import Messaging.EventManager;

public class MarketDataManager {

    private String symbol;
    private BinanceGateway gateway;
    private EventManager eventManager;

    public MarketDataManager(String symbol) {
        this.symbol = symbol;
        eventManager = new EventManager();
        gateway = new BinanceGateway(symbol);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void subscriberOrderBook() {
        gateway.startOrderBookEventStreaming(symbol, this);
    }

    public void subscribeTrades() {
        gateway.startAggTradesEventStreaming(symbol, this);
    }
}
