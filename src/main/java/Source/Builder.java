package Source;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;

import Analytics.AnalyticsManager;
import Analytics.CrossOverManager;
import Messaging.EventManager;
import Scheduling.ScheduleManager;

/**
 * Builder class to create managers and threads.
 */
public class Builder {
    private String symbol;
    private int window1;
    private int window2;
    private Double threshold;

    /**
     * Creates a Builder object with given symbol, window of first SMA and window of second SMA.
     */
    public Builder(String symbol, int window1, int window2, Double threshold) {
        this.symbol = symbol;
        this.window1 = window1;
        this.window2 = window2;
        this.threshold = threshold;
    }

    /**
     * Creates managers and executes threads.
     */
    public void start() throws SchedulerException {
        // Create Managers
        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager(symbol, eventManager);
        ScheduleManager scheduleManager = new ScheduleManager(eventManager);
        CrossOverManager crossOverManager = new CrossOverManager(window1, window2, threshold);
        AnalyticsManager analyticsManager = new AnalyticsManager(eventManager, scheduleManager);
        analyticsManager.addListener(crossOverManager);

        // Executor service for multithreading
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.execute(marketDataManager);
        threadPool.execute(analyticsManager);
    }
}
