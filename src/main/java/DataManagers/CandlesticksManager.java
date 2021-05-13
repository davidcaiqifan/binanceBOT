package DataManagers;

import java.util.Map;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import Candlesticks.CandlesticksCacher;

public class CandlesticksManager {

    private final CandlesticksCacher candlestickCacher;
    private final String symbol;
    private final CandlestickInterval interval;

    public CandlesticksManager(String symbol, CandlestickInterval interval) {
        this.candlestickCacher = new CandlesticksCacher(symbol, interval);
        this.symbol = symbol;
        this.interval = interval;
    }
    
    public void getCandlesticksData() {
        candlestickCacher.initializeCandlestickCache();
        candlestickCacher.startCandlestickEventStreaming();
    }
    
    public Map<Long, Candlestick> getCandlesticksCache() {
        return this.candlestickCacher.getCandlesticksCache();
    }
}