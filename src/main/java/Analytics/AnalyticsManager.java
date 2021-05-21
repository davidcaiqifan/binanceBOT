package Analytics;

import java.util.ArrayList;
import java.util.List;

import org.quartz.SchedulerException;
import com.binance.api.client.domain.account.Order;
import Messaging.EventBroker;
import Messaging.EventListener;
import Messaging.EventManager;
import Scheduling.ScheduleEvent;
import Scheduling.ScheduleManager;
import Source.OrderBook;

public class AnalyticsManager implements EventListener, Runnable {
    private EventBroker orderBookBroker;
    private EventBroker scheduleBroker;
    private List<EventListener> listeners = new ArrayList<>();
    private ScheduleManager scheduleManager;
    
    public AnalyticsManager(EventManager eventManager, ScheduleManager scheduleManager) {
        orderBookBroker = eventManager.getOrderBookBroker();
        scheduleBroker = eventManager.getScheduleBroker();
        this.scheduleManager = scheduleManager;
    }
    
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    
    public void broadcast(OrderBook orderBook) throws InterruptedException {
        for (EventListener listener : listeners) {
            listener.handleEvent(orderBook);
        }
    }
    
    public void broadcast(ScheduleEvent timer) throws InterruptedException {
        for (EventListener listener : listeners) {
            listener.handleEvent(timer);
        }
    }
    
    public void handleEvent(OrderBook orderBook) throws InterruptedException {
        broadcast(orderBook);
    }
    
    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        broadcast(timer);
    }

    /**
     * Initiates periodicCallBack in ScheduleManager.
     * Both SMA1 and SMA2 callback intervals are set at 2000 milliseconds.
     */
    protected void initialize() {
        try {
            scheduleManager.periodicCallBack(500, "sma1");
            scheduleManager.periodicCallBack(500, "sma2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
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
