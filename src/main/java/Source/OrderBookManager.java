package Source;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBookEntry;

import Messaging.EventManager;

public class OrderBookManager {

    private long orderBookLastUpdateId;

    private Source.OrderBook orderBookCache;
    private EventManager eventManager;

    public OrderBookManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Initializes the orderBookCache with two keys "BIDS" and "ASKS", each holding a
     * map of bid and ask entries respectively.
     */
    public void initializeOrderBookCache(com.binance.api.client.domain.market.OrderBook orderBook) {
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
     * Merges deltas with the current orderBook and publishes the resulting orderBook.
     */
    public void handleResponse(DepthEvent response) {
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
        return orderBookCache.getAsks();
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return orderBookCache.getBids();
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
     * Prints the cached order book / depth of a symbol as well as the best ask and bid price in the book.
     */
    private void printDepthCache() {
        System.out.println(orderBookCache);
        System.out.println("ASKS:");
        getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BIDS:");
        getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BEST ASK: " + toDepthCacheEntryString(getBestAsk()));
        System.out.println("BEST BID: " + toDepthCacheEntryString(getBestBid()));
    }

    private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    public Source.OrderBook getOrderBookCache() {
        return orderBookCache;
    }
}
