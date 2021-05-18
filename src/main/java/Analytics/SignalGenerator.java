package Analytics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import Messaging.EventManager;
import Scheduling.ScheduleEvent;
import Source.OrderBook;

public class SignalGenerator implements Runnable {
    
    private SimpleMovingAverage sma1;
    private SimpleMovingAverage sma2;
    private EventManager eventManager;
    private int currentPosition;
    private Double movingAvg1;
    private Double movingAvg2;
    private DescriptiveStatistics trades = new DescriptiveStatistics();
    
    public SignalGenerator(int period1, int period2, EventManager eventManager) {
        this.eventManager = eventManager;
        sma1 = new SimpleMovingAverage(period1, this, eventManager.getAggTradesBroker(), eventManager.getOrderBookBroker(), eventManager.getScheduleBroker());
        sma2 = new SimpleMovingAverage(period2, this, eventManager.getAggTradesBroker(), eventManager.getOrderBookBroker(), eventManager.getScheduleBroker());
    }
    
    private void initialize() {
        eventManager.addListener(sma1);
        eventManager.addListener(sma2);
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
                trades.addValue(sma1.orderBookCache.lastEntry().getValue().getBestAsk().getKey().doubleValue());
            }
        } else {
            if (movingAvg1 > movingAvg2) {
                System.out.println("Buy Signal");
                currentPosition = 1;
                trades.addValue(-1 * sma1.orderBookCache.lastEntry().getValue().getBestAsk().getKey()
                        .doubleValue());
            }
        }
    }
    
    public DescriptiveStatistics getTrades() {
        return trades;
    }

    @Override
    public void run() {
        initialize();
        while (true) {
            try {
                sma1.handleEvent((OrderBook) sma1.orderBookBroker.get());
                sma1.handleEvent((ScheduleEvent) sma1.scheduleQueue.get());
                sma2.handleEvent((OrderBook) sma2.orderBookBroker.get());
                sma2.handleEvent((ScheduleEvent) sma2.scheduleQueue.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
}
