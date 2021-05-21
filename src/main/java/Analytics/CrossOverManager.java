package Analytics;

import java.util.NavigableMap;
import java.util.TreeMap;

import Messaging.EventListener;
import Scheduling.ScheduleEvent;
import Source.OrderBook;

/**
 * Analytics Manager consolidates two simple moving averages, the signal generator, and schedule manager.
 */
public class CrossOverManager implements EventListener {
    private SimpleMovingAverage sma1;
    private SimpleMovingAverage sma2;
    private Double threshold;
    private NavigableMap<Long, OrderBook> orderBookCache = new TreeMap<>();
    private long orderBookId = 0L;
    private SignalGenerator signalGenerator;

    /**
     * Creates a AnalyticManager.
     * Creates two SimpleMovingAverage objects based on windows given and creates a SignalGenerator with the two SMAs.
     */
    public CrossOverManager(int window1, int window2, Double threshold) {
        sma1 = new SimpleMovingAverage(window1);
        sma2 = new SimpleMovingAverage(window2);
        this.threshold = threshold;
        signalGenerator = new SignalGenerator(this);
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
        signalGenerator.generateSignal(threshold);
    }

    /**
     * @return the orderBookCache for usage by Signal Generator.
     */
    public NavigableMap<Long, OrderBook> getOrderBookCache() {
        return orderBookCache;
    }

    public SimpleMovingAverage getSma1() {
        return sma1;
    }

    public SimpleMovingAverage getSma2() {
        return sma2;
    }
}
