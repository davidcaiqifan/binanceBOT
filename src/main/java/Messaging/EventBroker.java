package Messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;

public class EventBroker<T> {
    private BlockingQueue<T> eventQueue = new ArrayBlockingQueue<>(1024);
    private List<EventListener> listenerList = new ArrayList<>();
    
    public void addEvent(T event) throws InterruptedException {
        eventQueue.put(event);
    }

    public void broadcast() {
        if (eventQueue.isEmpty()) {
            System.out.println("No events to broadcast.");
        } else {
            while (!eventQueue.isEmpty()) {
                T event = eventQueue.remove();
                sendToSubscribers(event);
            }
        }
    }
    
    public void sendToSubscribers(T event) {
        for (EventListener listener : listenerList) {
            if (event instanceof AggTrade) {
                listener.handleEvent((AggTrade) event);
            } else if (event instanceof OrderBook) {
                listener.handleEvent((OrderBook) event);
            }
        }
    }

    public void addListener(EventListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        } else {
            System.out.println("Listener already exists in list.");
        }
    }
    
    public void removeListener(EventListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        } else {
            System.out.println("Listener does not exist in list");
        }
    }
    
}
