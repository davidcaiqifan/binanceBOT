package Analytics;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBookEntry;

import Messaging.EventListener;

public abstract class AnalyticManager implements EventListener {
    protected NavigableMap<Long, AggTrade> aggTradesCache = new TreeMap<>();
    protected Map<String, NavigableMap<BigDecimal, BigDecimal>> orderBookCache =
            new HashMap<>();
    private long orderBookLastUpdateId = 0;

    public AnalyticManager() {
        orderBookCache.put("ASKS", new TreeMap<>());
        orderBookCache.put("BIDS", new TreeMap<>());
    }

    @Override
    public void handleEvent(AggTradeEvent aggTradeEvent) {
        Long aggregatedTradeId = aggTradeEvent.getAggregatedTradeId();
        AggTrade updateAggTrade = (AggTrade) aggTradesCache.get(aggregatedTradeId);
        if (updateAggTrade == null) {
            updateAggTrade = new AggTrade();
        }
        updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
        updateAggTrade.setPrice(aggTradeEvent.getPrice());
        updateAggTrade.setQuantity(aggTradeEvent.getQuantity());
        updateAggTrade.setFirstBreakdownTradeId(aggTradeEvent.getFirstBreakdownTradeId());
        updateAggTrade.setLastBreakdownTradeId(aggTradeEvent.getLastBreakdownTradeId());
        updateAggTrade.setBuyerMaker(aggTradeEvent.isBuyerMaker());

        // Store the updated agg trade in the cache
        aggTradesCache.put(aggregatedTradeId, updateAggTrade);
        // System.out.println(updateAggTrade);
    }

    @Override
    public void handleEvent(DepthEvent depthEvent) {
        if (depthEvent.getUpdateId() > orderBookLastUpdateId) {
            orderBookLastUpdateId = depthEvent.getUpdateId();
            updateOrderBook(getAsks(), depthEvent.getAsks());
            updateOrderBook(getBids(), depthEvent.getBids());
            printOrderBookCache();
        }
    }

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
     * Prints the cached order book / depth of a symbol as well as the best ask and bid price in the book.
     */
    private void printOrderBookCache() {
        System.out.println(orderBookCache);
        System.out.println("ASKS:");
        getAsks().entrySet().forEach(entry -> System.out.println(toOrderBookCacheEntryString(entry)));
        System.out.println("BIDS:");
        getBids().entrySet().forEach(entry -> System.out.println(toOrderBookCacheEntryString(entry)));
    }

    /**
     * Pretty prints an order book entry in the format "price / quantity".
     */
    private static String toOrderBookCacheEntryString(Map.Entry<BigDecimal, BigDecimal> orderBookCacheEntry) {
        return orderBookCacheEntry.getKey().toPlainString() + " / " + orderBookCacheEntry.getValue();
    }
}
