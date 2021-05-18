package Analytics;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;

import Messaging.EventListener;
import Source.OrderBook;

public abstract class AnalyticManager implements EventListener, Runnable {
    protected NavigableMap<Long, AggTrade> aggTradesCache = new TreeMap<>();
    protected NavigableMap<Long, OrderBook> orderBookCache =
            new TreeMap<>();
    private long orderBookId = 0L;
    
//    public void handleEvent(AggTradeEvent aggTradeEvent) throws InterruptedException {
//        Long aggregatedTradeId = aggTradeEvent.getAggregatedTradeId();
//        AggTrade updateAggTrade = (AggTrade) aggTradesCache.get(aggregatedTradeId);
//        if (updateAggTrade == null) {
//            updateAggTrade = new AggTrade();
//        }
//        updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
//        updateAggTrade.setPrice(aggTradeEvent.getPrice());
//        updateAggTrade.setQuantity(aggTradeEvent.getQuantity());
//        updateAggTrade.setFirstBreakdownTradeId(aggTradeEvent.getFirstBreakdownTradeId());
//        updateAggTrade.setLastBreakdownTradeId(aggTradeEvent.getLastBreakdownTradeId());
//        updateAggTrade.setBuyerMaker(aggTradeEvent.isBuyerMaker());
//
//        // Store the updated agg trade in the cache
//        aggTradesCache.put(aggregatedTradeId, updateAggTrade);
//        // System.out.println(updateAggTrade);
//    }
    
    public void handleEvent(OrderBook orderBook) throws InterruptedException {
        orderBookCache.put(orderBookId++, orderBook);
    }
}
