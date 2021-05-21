package Source;

import Messaging.EventManager;

/**
 * Manages market data from Binance.
 */
public class MarketDataManager implements Runnable {

    private String symbol;
    private BinanceGateway gateway;
    private EventManager eventManager;

    /**
     * Initializes a MarketDataManager with given symbol and takes in reference to an EventManager.
     */
    public MarketDataManager(String symbol, EventManager eventManager) {
        this.symbol = symbol;
        this.eventManager = eventManager;
        gateway = new BinanceGateway(symbol, eventManager);
    }

    /**
     * Starts streaming of orderbook events from Binance.
     */
    public void subscribeOrderBook() {
        gateway.startOrderBookEventStreaming();
    }

    /**
     * Starts streaming of events from Binance.
     */
    public void subscribeTrades() {
        gateway.startAggTradesEventStreaming();
    }

    @Override
    public void run() {
        subscribeOrderBook();
    }
}
