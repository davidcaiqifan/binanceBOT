package Analytics;

import Messaging.EventBroker;
import Messaging.EventListener;
import Messaging.EventManager;
import Messaging.TradeEventListener;
import Scheduling.ScheduleEvent;
import Scheduling.ScheduleManager;
import com.binance.api.client.domain.event.AggTradeEvent;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;

public class TradeManager implements TradeEventListener, Runnable {
    private EventBroker aggTradesBroker;
    private EventBroker scheduleBroker;
    private List<TradeEventListener> listeners = new ArrayList<>();
    private ScheduleManager scheduleManager;

    /**
     * Trade Manager polls events from the broker's queue to broadcast to all subscribed listeners.
     */
    public TradeManager(EventManager eventManager, ScheduleManager scheduleManager) {
        aggTradesBroker = eventManager.getAggTradesBroker();
        scheduleBroker = eventManager.getScheduleBroker();
        this.scheduleManager = scheduleManager;
    }

    public void addListener(TradeEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Sends orderBook to all subscribed listeners.
     */
    public void broadcast(AggTradeEvent aggTradeEvent) throws InterruptedException {
        for (TradeEventListener listener : listeners) {
            listener.handleEvent(aggTradeEvent);
        }
    }

    /**
     * Sends timer to all subscribed listeners.
     */
    public void broadcast(ScheduleEvent timer) throws InterruptedException {
        for (TradeEventListener listener : listeners) {
            listener.handleEvent(timer);
        }
    }

    public void handleEvent(AggTradeEvent aggTradeEvent) throws InterruptedException {
        broadcast(aggTradeEvent);
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
                handleEvent((AggTradeEvent) aggTradesBroker.get());
                handleEvent((ScheduleEvent) scheduleBroker.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
