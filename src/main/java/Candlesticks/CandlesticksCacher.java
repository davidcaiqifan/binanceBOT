package Candlesticks;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;


public class CandlesticksCacher {
    /**
     * Key is the start/open time of the candle, and the value contains candlestick date.
     */
    private String symbol;
    private CandlestickInterval interval;
    private Map<Long, Candlestick> candlesticksCache;

    public CandlesticksCacher(String symbol, CandlestickInterval interval) {
        this.symbol = symbol;
        this.interval = interval;
    }

    /**
     * Initializes the candlestick cache by using the REST API.
     */
    public void initializeCandlestickCache() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<Candlestick> candlestickBars = client.getCandlestickBars(symbol.toUpperCase(), interval);

        this.candlesticksCache = new TreeMap<>();
        for (Candlestick candlestickBar : candlestickBars) {
            candlesticksCache.put(candlestickBar.getOpenTime(), candlestickBar);
        }
    }

    /**
     * Begins streaming of depth events.
     */
    public void startCandlestickEventStreaming() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
            Long openTime = response.getOpenTime();
            Candlestick updateCandlestick = candlesticksCache.get(openTime);
            if (updateCandlestick == null) {
                // new candlestick
                updateCandlestick = new Candlestick();
            }
            // update candlestick with the stream data
            updateCandlestick.setOpenTime(response.getOpenTime());
            updateCandlestick.setOpen(response.getOpen());
            updateCandlestick.setLow(response.getLow());
            updateCandlestick.setHigh(response.getHigh());
            updateCandlestick.setClose(response.getClose());
            updateCandlestick.setCloseTime(response.getCloseTime());
            updateCandlestick.setVolume(response.getVolume());
            updateCandlestick.setNumberOfTrades(response.getNumberOfTrades());
            updateCandlestick.setQuoteAssetVolume(response.getQuoteAssetVolume());
            updateCandlestick.setTakerBuyQuoteAssetVolume(response.getTakerBuyQuoteAssetVolume());
            updateCandlestick.setTakerBuyBaseAssetVolume(response.getTakerBuyQuoteAssetVolume());

            // Store the updated candlestick in the cache
            candlesticksCache.put(openTime, updateCandlestick);
            System.out.println(updateCandlestick);
        });
    }

    /**
     * @return a klines/candlestick cache, containing the open/start time of the candlestick as the key,
     * and the candlestick data as the value.
     */
    public Map<Long, Candlestick> getCandlesticksCache() {
        return candlesticksCache;
    }

}
