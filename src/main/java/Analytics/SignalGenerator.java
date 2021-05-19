package Analytics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Signal Generator keeps track of two moving averages and generates trade signals based on MA crossover strategy.
 */
public class SignalGenerator {
    
    private SimpleMovingAverage sma1;
    private SimpleMovingAverage sma2;
    private int currentPosition;
    private Double movingAvg1;
    private Double movingAvg2;
    private DescriptiveStatistics trades = new DescriptiveStatistics();
    private AnalyticManager am;

    /**
     * Initializes SignalGenerator with two simple moving averages and an analytics manager.
     * Analytics Manager is referenced to access orderBookCache to retrieve latest prices for PnL analytics.
     */
    public SignalGenerator(SimpleMovingAverage sma1, SimpleMovingAverage sma2, AnalyticManager am) {
        this.sma1 = sma1;
        this.sma2 = sma2;
        this.am = am;
    }

    /**
     * Generates Trade signal using MA crossover strategy.
     * currentPosition is set to zero if there is insufficient data, set to one if sma1 > sma2, and set to -1 if sma2 > sma1.
     */
    public void generateSignal() {
        movingAvg1 = sma1.getMovingAverage();
        movingAvg2 = sma2.getMovingAverage();
        System.out.println(currentPosition);
        if (movingAvg2 == 0 || movingAvg1 == 0) {
            currentPosition = 0;
        } else if (currentPosition == 0) {
            if (movingAvg1 > movingAvg2) {
                currentPosition = 1;
            } else if (movingAvg2 > movingAvg1) {
                currentPosition = -1;
            }
        } else if (currentPosition == 1) {
            if (movingAvg2 > movingAvg1) {
                System.out.println("Sell Signal");
                currentPosition = -1;
                trades.addValue(am.getOrderBookCache().lastEntry().getValue().getBestAsk().getKey().doubleValue());
            }
        } else {
            if (movingAvg1 > movingAvg2) {
                System.out.println("Buy Signal");
                currentPosition = 1;
                trades.addValue(-1 * am.getOrderBookCache().lastEntry().getValue().getBestAsk().getKey()
                        .doubleValue());
            }
        }
    }

    /**
     * Returns record of trades done for analysis of PnL.
     */
    public DescriptiveStatistics getTrades() {
        return trades;
    }
}
