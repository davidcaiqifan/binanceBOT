package Source;

import Messaging.EventManager;

public class MarketDataManager implements Runnable {

    private String symbol;
    private BinanceGateway gateway;
    private EventManager eventManager;

    public MarketDataManager(String symbol, EventManager eventManager) {
        this.symbol = symbol;
        this.eventManager = eventManager;
        gateway = new BinanceGateway(symbol);
    }

    public void subscribeOrderBook() {
        gateway.startOrderBookEventStreaming(symbol, eventManager);
    }

    public void subscribeTrades() {
        gateway.startAggTradesEventStreaming(symbol, eventManager);
    }

    @Override
    public void run() {
        subscribeOrderBook();
    }
}
