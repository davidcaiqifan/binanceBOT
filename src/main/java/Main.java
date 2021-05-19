
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;

import Analytics.AnalyticManager;
import Analytics.SignalGenerator;
import Analytics.SimpleMovingAverage;
import Messaging.EventManager;
import Scheduling.ScheduleManager;
import Source.Builder;
import Source.MarketDataManager;

public class Main {
    public static void main(String[] args) throws SchedulerException {
        Builder builder = new Builder("ETHBTC", 5, 10);
        builder.start();
    }
}


