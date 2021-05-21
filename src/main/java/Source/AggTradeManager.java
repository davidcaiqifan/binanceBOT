package Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import Messaging.EventManager;

public class AggTradeManager {

    private Map<Long, AggTrade> aggTradesCache;
    private EventManager eventManager;

    public AggTradeManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void initializeAggTradesCache(List<AggTrade> aggTrades) {
        this.aggTradesCache = new HashMap<>();
        for (AggTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }
    
    public void handleResponse(AggTradeEvent response) {
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
        // System.out.println(updateAggTrade);

        // Publish updated agg trade
        try {
            eventManager.publish(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesCache;
    }
}
