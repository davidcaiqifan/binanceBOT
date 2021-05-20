package Analytics;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.quartz.SchedulerException;

import Messaging.EventBroker;
import Messaging.EventListener;
import Messaging.EventManager;
import Scheduling.ScheduleEvent;
import Scheduling.ScheduleManager;
import Source.OrderBook;

/**
 * Analytics Manager consolidates two simple moving averages, the signal generator, and schedule manager.
 */
public class AnalyticManager implements EventListener, Runnable {
    private SimpleMovingAverage sma1;
    private SimpleMovingAverage sma2;
    private EventBroker orderBookBroker;
    private EventBroker scheduleBroker;
    private NavigableMap<Long, OrderBook> orderBookCache = new TreeMap<>();
    private long orderBookId = 0L;
    private SignalGenerator signalGenerator;
    private ScheduleManager scheduleManager;

    /**
     * Creates a AnalyticManager.
     * Creates two SimpleMovingAverage objects based on windows given and creates a SignalGenerator with the two SMAs.
     */
    public AnalyticManager(int window1, int window2, EventManager eventManager, ScheduleManager scheduleManager) {
        sma1 = new SimpleMovingAverage(window1);
        sma2 = new SimpleMovingAverage(window2);
        orderBookBroker = eventManager.getOrderBookBroker();
        scheduleBroker = eventManager.getScheduleBroker();
        this.scheduleManager = scheduleManager;
        signalGenerator = new SignalGenerator(sma1, sma2, this);
    }

    /**
     * Initiates periodicCallBack in ScheduleManager.
     * Both SMA1 and SMA2 callback intervals are set at 2000 milliseconds.
     */
    private void initialize() {
        try {
            scheduleManager.periodicCallBack(500, "sma1");
            scheduleManager.periodicCallBack(500, "sma2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles order book event by adding order book to the order book cache.
     */
    public void handleEvent(OrderBook orderBook) throws InterruptedException {
        orderBookCache.put(orderBookId++, orderBook);
    }

    /**
     * Updates prices of the respective SMA's recentPrice list, then calls SignalGenerator to generate a trade signal.
     * Differentiates schedule events for different SMAs based on their tags.
     * @throws InterruptedException
     */
    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        if (timer.getTag().equals("sma1")) {
            sma1.updateRecentPrices(orderBookCache);
        } else if (timer.getTag().equals("sma2")) {
            sma2.updateRecentPrices(orderBookCache);
        }
        signalGenerator.generateSignal();
    }

    /**
     * @return the orderBookCache for usage by Signal Generator.
     */
    public NavigableMap<Long, OrderBook> getOrderBookCache() {
        return orderBookCache;
    }

    /**
     * Gets orderbook and schedule events from their respective broker's queues, and handles these events.
     */
    @Override
    public void run() {
        initialize();
        while (true) {
            try {
                handleEvent((OrderBook) orderBookBroker.get());
                handleEvent((ScheduleEvent) scheduleBroker.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
