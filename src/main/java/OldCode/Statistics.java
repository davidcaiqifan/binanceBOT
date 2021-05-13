package OldCode;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerStatistics;

public class Statistics {
    static String getLatestPrice(BinanceApiRestClient client, String symbol) {
        TickerStatistics tickerStatistics = client.get24HrPriceStatistics(symbol);
        return tickerStatistics.getLastPrice();
    }
    
    static List<String> getDailyClosingPrices(BinanceApiRestClient client, String symbol) {
        List<Candlestick> candlesticks = client.getCandlestickBars(symbol, CandlestickInterval.DAILY);
        List<String> closingPrices = new ArrayList<>();
        for (Candlestick candlestick : candlesticks) {
            closingPrices.add(candlestick.getClose());
        }
        return closingPrices;
    }

    static Double getFiveDaySMA(BinanceApiRestClient client, String symbol) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        List<String> closingPrices = getDailyClosingPrices(client, symbol);
        for(int i = closingPrices.size() - 5; i < closingPrices.size(); i++) {
            stats.addValue(Double.parseDouble(closingPrices.get(i)));
        }
        double mean = stats.getMean();
        return mean;
    }

    static Double getTenDaySMA(BinanceApiRestClient client, String symbol) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        List<String> closingPrices = getDailyClosingPrices(client, symbol);
        for(int i = closingPrices.size() - 10; i < closingPrices.size(); i++) {
            stats.addValue(Double.parseDouble(closingPrices.get(i)));
        }
        double mean = stats.getMean();
        return mean;
    }


}
