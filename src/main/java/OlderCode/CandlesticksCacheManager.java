package OlderCode;

import java.util.Map;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import OlderCode.CandlesticksGateway;

public class CandlesticksCacheManager {

    private final CandlesticksGateway candlestickCacher;
    private final String symbol;
    private final CandlestickInterval interval;

    public CandlesticksCacheManager(String symbol, CandlestickInterval interval) {
        this.candlestickCacher = new CandlesticksGateway(symbol, interval);
        this.symbol = symbol;
        this.interval = interval;
        candlestickCacher.initializeCandlestickCache();
    }

    public void startCandlesticksEventStream() {
        candlestickCacher.startCandlestickEventStreaming();
    }
    
    public Map<Long, Candlestick> getCandlesticksCache() {
        return this.candlestickCacher.getCandlesticksCache();
    }
}