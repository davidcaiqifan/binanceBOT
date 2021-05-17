package Source;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import Messaging.EventManager;

public class BinanceGateway {

    private BinanceApiRestClient client;

    private long orderBookLastUpdateId;

    private Map<Long, AggTrade> aggTradesCache;
    private Source.OrderBook orderBookCache;

    public BinanceGateway(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        client = factory.newRestClient();
        initializeOrderBookCache(symbol);
        initializeAggTradesCache(symbol);
    }

    /**
     * Initializes the aggTrades cache by using the REST API.
     */
    public void initializeAggTradesCache(String symbol) {
        List<AggTrade> aggTrades = client.getAggTrades(symbol.toUpperCase());

        this.aggTradesCache = new HashMap<>();
        for (AggTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }

    /**
     * Initializes the depth cache by using the REST API.
     */
    public void initializeOrderBookCache(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        OrderBook orderBook = client.getOrderBook(symbol.toUpperCase(), 10);

        this.orderBookCache = new Source.OrderBook();
        this.orderBookLastUpdateId = orderBook.getLastUpdateId();

        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry ask : orderBook.getAsks()) {
            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));
        }
        orderBookCache.put("ASKS", asks);

        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry bid : orderBook.getBids()) {
            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));
        }
        orderBookCache.put("BIDS", bids);
    }

    /**
     * Begins streaming of Agg Trade Events.
     */
    public void startAggTradesEventStreaming(String symbol, EventManager eventManager) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onAggTradeEvent(symbol.toLowerCase(), response -> {
            Long aggregatedTradeId = response.getAggregatedTradeId();
            AggTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId);
            if (updateAggTrade == null) {
                // new agg trade
                updateAggTrade = new AggTrade();
            }
            updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
            updateAggTrade.setPrice(response.getPrice());
            updateAggTrade.setQuantity(response.getQuantity());
            updateAggTrade.setFirstBreakdownTradeId(response.getFirstBreakdownTradeId());
            updateAggTrade.setLastBreakdownTradeId(response.getLastBreakdownTradeId());
            updateAggTrade.setBuyerMaker(response.isBuyerMaker());

            // Store the updated agg trade in the cache
            aggTradesCache.put(aggregatedTradeId, updateAggTrade);

            // Publish updated agg trade
            try {
                eventManager.publish(response);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Begins streaming of order book events.
     */
    public void startOrderBookEventStreaming(String symbol, EventManager eventManager) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onDepthEvent(symbol.toLowerCase(), response -> {
            if (response.getUpdateId() > orderBookLastUpdateId) {
                orderBookLastUpdateId = response.getUpdateId();
                updateOrderBook(orderBookCache.getAsks(), response.getAsks());
                updateOrderBook(orderBookCache.getBids(), response.getBids());
                
                try {
                    eventManager.publish(orderBookCache);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Updates an order book (bids or asks) with a delta received from the server.
     *
     * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
     */
    private void updateOrderBook(NavigableMap<BigDecimal,
            BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this level
                lastOrderBookEntries.remove(price);
            } else {
                lastOrderBookEntries.put(price, qty);
            }
        }
    }
}
