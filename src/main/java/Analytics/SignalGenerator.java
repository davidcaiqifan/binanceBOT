package Analytics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SignalGenerator {
    
    private SimpleMovingAverage sma1;
    private SimpleMovingAverage sma2;
    private int currentPosition;
    private Double movingAvg1;
    private Double movingAvg2;
    private DescriptiveStatistics trades = new DescriptiveStatistics();
    private AnalyticManager am;
    
    public SignalGenerator(SimpleMovingAverage sma1, SimpleMovingAverage sma2, AnalyticManager am) {
        this.sma1 = sma1;
        this.sma2 = sma2;
        this.am = am;
    }

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
                trades.addValue(am.orderBookCache.lastEntry().getValue().getBestAsk().getKey().doubleValue());
            }
        } else {
            if (movingAvg1 > movingAvg2) {
                System.out.println("Buy Signal");
                currentPosition = 1;
                trades.addValue(-1 * am.orderBookCache.lastEntry().getValue().getBestAsk().getKey()
                        .doubleValue());
            }
        }
    }
    
    public DescriptiveStatistics getTrades() {
        return trades;
    }
    
    
}
