package DataManagers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.binance.api.client.domain.market.AggTrade;
import AggTrades.AggTradesCacher;
import pubsub.PubSubManager;

public class AggTradesManager {
    
    private final AggTradesCacher aggTradesCacher;
    private final String symbol;
    private final PubSubManager pubSubManager;

    public AggTradesManager(String symbol, PubSubManager pubSubManager) {
        this.symbol = symbol;
        this.pubSubManager = pubSubManager;
        aggTradesCacher = new AggTradesCacher(symbol, pubSubManager);
        aggTradesCacher.initializeAggTradesCache();
    }

    public void startAggTradesEventStream() {
        aggTradesCacher.startAggTradesEventStreaming();
    }
    
    public List<AggTrade> getAggTradeEventList() {
        return aggTradesCacher.getAggTradesEventList();
    }

    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesCacher.getAggTradesCache();
    }
}
