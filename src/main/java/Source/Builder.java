package Source;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;

import Analytics.AnalyticManager;
import Messaging.EventManager;
import Scheduling.ScheduleManager;

/**
 * Builder class to create managers and threads.
 */
public class Builder {
    private String symbol;
    private int window1;
    private int window2;

    /**
     * Creates a Builder object with given symbol, window of first SMA and window of second SMA.
     */
    public Builder(String symbol, int window1, int window2) {
        this.symbol = symbol;
        this.window1 = window1;
        this.window2 = window2;
    }

    /**
     * Creates managers and executes threads.
     */
    public void start() throws SchedulerException {
        // Create Managers
        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager(symbol, eventManager);
        ScheduleManager scheduleManager = new ScheduleManager(eventManager);
        AnalyticManager analyticManager = new AnalyticManager(window1, window2, eventManager, scheduleManager);

        // Executor service for multithreading
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.execute(marketDataManager);
        threadPool.execute(analyticManager);
    }
}
