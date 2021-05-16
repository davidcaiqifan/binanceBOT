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

public class BinanceGateway {

    private BinanceApiRestClient client;

    private long orderBookLastUpdateId;

    private Map<Long, AggTrade> aggTradesCache;
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> orderBookCache;

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

        this.orderBookCache = new HashMap<>();
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
    public void startAggTradesEventStreaming(String symbol, MarketDataManager marketDataManager) {
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
            System.out.println(updateAggTrade);

            // Publish updated agg trade
            try {
                marketDataManager.getEventManager().publish(response);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Begins streaming of order book events.
     */
    public void startOrderBookEventStreaming(String symbol, MarketDataManager marketDataManager) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onDepthEvent(symbol.toLowerCase(), response -> {
            if (response.getUpdateId() > orderBookLastUpdateId) {
                System.out.println(response);
                orderBookLastUpdateId = response.getUpdateId();
                updateOrderBook(getAsks(), response.getAsks());
                updateOrderBook(getBids(), response.getBids());
                printOrderBookCache();

                try {
                    marketDataManager.getEventManager().publish(response);
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

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return orderBookCache.get("ASKS");
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return orderBookCache.get("BIDS");
    }

    /**
     * @return the best ask in the order book
     */
    private Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
        return getAsks().lastEntry();
    }

    /**
     * @return the best bid in the order book
     */
    private Map.Entry<BigDecimal, BigDecimal> getBestBid() {
        return getBids().firstEntry();
    }

    /**
     * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
     */
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getOrderBookCache() {
        return orderBookCache;
    }

    /**
     * Prints the cached order book / depth of a symbol as well as the best ask and bid price in the book.
     */
    private void printOrderBookCache() {
        System.out.println(orderBookCache);
        System.out.println("ASKS:");
        getAsks().entrySet().forEach(entry -> System.out.println(toOrderBookCacheEntryString(entry)));
        System.out.println("BIDS:");
        getBids().entrySet().forEach(entry -> System.out.println(toOrderBookCacheEntryString(entry)));
        System.out.println("BEST ASK: " + toOrderBookCacheEntryString(getBestAsk()));
        System.out.println("BEST BID: " + toOrderBookCacheEntryString(getBestBid()));
    }

    /**
     * Pretty prints an order book entry in the format "price / quantity".
     */
    private static String toOrderBookCacheEntryString(Map.Entry<BigDecimal, BigDecimal> orderBookCacheEntry) {
        return orderBookCacheEntry.getKey().toPlainString() + " / " + orderBookCacheEntry.getValue();
    }
}
