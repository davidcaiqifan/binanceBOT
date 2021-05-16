package Source;

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
