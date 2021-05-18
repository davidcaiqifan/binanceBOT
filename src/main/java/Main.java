
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;

import Analytics.AnalyticManager;
import Analytics.SignalGenerator;
import Analytics.SimpleMovingAverage;
import Messaging.EventManager;
import Scheduling.ScheduleManager;
import Source.MarketDataManager;

public class Main {
    public static void main(String[] args) throws SchedulerException {
        
        // Create Managers
        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC", eventManager);
        ScheduleManager scheduleManager = new ScheduleManager(eventManager);
        
        // Create signal generator
        SignalGenerator signalGenerator = new SignalGenerator(5, 10, eventManager);

        // Executor service for multithreading
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.execute(marketDataManager);
        threadPool.execute(scheduleManager);
        threadPool.execute(signalGenerator);
        // need to shutdown?
    }
}


