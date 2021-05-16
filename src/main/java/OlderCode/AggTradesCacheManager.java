package OlderCode;

import java.util.List;
import java.util.Map;

import com.binance.api.client.domain.market.AggTrade;

public class AggTradesCacheManager {
    
    private final AggTradesGateway aggTradesGateway;
    private final String symbol;
    private final PubSubManager pubSubManager;

    public AggTradesCacheManager(String symbol, PubSubManager pubSubManager) {
        this.symbol = symbol;
        this.pubSubManager = pubSubManager;
        aggTradesGateway = new AggTradesGateway(symbol, pubSubManager);
        aggTradesGateway.initializeAggTradesCache();
    }

    public void startAggTradesEventStream() {
        aggTradesGateway.startAggTradesEventStreaming();
    }
    
    public List<AggTrade> getAggTradeEventList() {
        return aggTradesGateway.getAggTradesEventList();
    }

    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesGateway.getAggTradesCache();
    }
}
