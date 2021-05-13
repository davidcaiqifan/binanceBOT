package DataManagers;

import java.util.Map;

import com.binance.api.client.domain.market.AggTrade;
import AggTrades.AggTradesCacher;

public class AggTradesManager {
    
    private final AggTradesCacher aggTradesCacher;
    private final String symbol;

    public AggTradesManager(String symbol) {
        this.symbol = symbol;
        aggTradesCacher = new AggTradesCacher(symbol);
    }
    
    public void getAggTradesData() {
        aggTradesCacher.initializeAggTradesCache();
        aggTradesCacher.startAggTradesEventStreaming();
    }
    
    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesCacher.getAggTradesCache();
    }
}
