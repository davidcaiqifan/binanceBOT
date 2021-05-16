
import org.quartz.SchedulerException;
import Analytics.AnalyticManager;
import Analytics.SimpleMovingAverage;
import Messaging.EventManager;
import Scheduling.ScheduleManager;
import Source.MarketDataManager;

public class Main {
    public static void main(String[] args) throws InterruptedException, SchedulerException {
        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC");
        EventManager eventManager = marketDataManager.getEventManager();
        ScheduleManager scheduleManager = new ScheduleManager(eventManager);
        marketDataManager.subscribeTrades();
        AnalyticManager smaFive = new SimpleMovingAverage(5);
        eventManager.addListener(smaFive);
        scheduleManager.periodicCallBack(1000);
    }
}


