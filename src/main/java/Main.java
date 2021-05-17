
import org.quartz.SchedulerException;
import Analytics.AnalyticManager;
import Analytics.SimpleMovingAverage;
import Messaging.EventManager;
import Scheduling.ScheduleManager;
import Source.MarketDataManager;

public class Main {
    public static void main(String[] args) throws InterruptedException, SchedulerException {
        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC", eventManager);
        ScheduleManager scheduleManager = new ScheduleManager(eventManager);
        marketDataManager.subscribeOrderBook();
        AnalyticManager smaFive = new SimpleMovingAverage(5);
        eventManager.addListener(smaFive);
        scheduleManager.periodicCallBack(1000);
    }
}

// create executor service - give it one thread, pass runnable to broadcast


