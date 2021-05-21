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
    // trades not implemented yet, still a work in progress!
    private DescriptiveStatistics trades = new DescriptiveStatistics();
    private CrossOverManager am;

    /**
     * Initializes SignalGenerator with two simple moving averages and an analytics manager.
     * Analytics Manager is referenced to access orderBookCache to retrieve latest prices for PnL analytics.
     */
    public SignalGenerator(CrossOverManager am) {
        this.sma1 = am.getSma1();
        this.sma2 = am.getSma2();
        this.am = am;
    }

    /**
     * Generates Trade signal using MA crossover strategy.
     * currentPosition is set to zero if there is insufficient data, set to one if sma1 > sma2, and set to -1 if sma2 > sma1.
     */
    public void generateSignal(Double threshold) {
        movingAvg1 = sma1.getMovingAverage();
        movingAvg2 = sma2.getMovingAverage();
        System.out.println(currentPosition);
        if (movingAvg2 == 0 || movingAvg1 == 0) {
            currentPosition = 0;
        } else if (currentPosition == 0) {
            if (movingAvg1 > movingAvg2 + threshold) {
                currentPosition = 1;
            } else if (movingAvg2 > movingAvg1 + threshold) {
                currentPosition = -1;
            }
        } else if (currentPosition == 1) {
            if (movingAvg2 > movingAvg1 + threshold) {
                System.out.println("Sell Signal");
                currentPosition = -1;
            }
        } else {
            if (movingAvg1 > movingAvg2 + threshold) {
                System.out.println("Buy Signal");
                currentPosition = 1;
            }
        }
    }
    
    protected SimpleMovingAverage getSma1() {
        return sma1;
    }

    protected SimpleMovingAverage getSma2() {
        return sma2;
    }
    
    protected int getCurrentPosition() {
        return currentPosition;
    }
}
