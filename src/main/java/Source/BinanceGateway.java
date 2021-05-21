package Source;

import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;

import Messaging.EventManager;

/**
 * Binance Gateway connects to the Binance REST and WebSocket APIs.
 */
public class BinanceGateway {

    private String symbol;
    private OrderBookManager orderBookManager;
    private AggTradeManager aggTradeManager;

    /**
     * Creates a Binance Gateway with given symbol and initializes OrderBook and AggTrade cache
     */
    public BinanceGateway(String symbol, EventManager eventManager) {
        this.symbol = symbol;
        orderBookManager = new OrderBookManager(eventManager);
        aggTradeManager = new AggTradeManager(eventManager);
        initializeOrderBookCache();
        initializeAggTradesCache();
    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */
    public void initializeAggTradesCache() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<AggTrade> aggTrades = client.getAggTrades(symbol.toUpperCase());

        aggTradeManager.initializeAggTradesCache(aggTrades);
    }

    /**
     * Initializes the depth cache by using the REST API.
     */
    public void initializeOrderBookCache() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        OrderBook orderBook = client.getOrderBook(symbol.toUpperCase(), 10);

        orderBookManager.initializeOrderBookCache(orderBook);
    }

    /**
     * Begins streaming of Agg Trade Events using WebSocket API.
     */
    public void startAggTradesEventStreaming() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onAggTradeEvent(symbol.toLowerCase(), response -> {
            aggTradeManager.handleResponse(response);
        });
    }

    /**
     * Begins streaming of order book events using WebSocket API.
     */
    public void startOrderBookEventStreaming() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onDepthEvent(symbol.toLowerCase(), response -> {
            orderBookManager.handleResponse(response);
        });
    }
}
