package Messaging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Event Broker implements a BlockingQueue.
 * EventManager publishes events to the queue and EventListeners get events from the queue.
 */
public class EventBroker<T> {
    private BlockingQueue<T> eventQueue = new ArrayBlockingQueue<>(1024);

    /**
     * Adds the event to the BlockingQueue.
     */
    public void addEvent(T event) throws InterruptedException {
        eventQueue.put(event);
    }

    /**
     * Returns the latest event in the queue.
     * Waits for event if queue is empty.
     */
    public T get() throws InterruptedException {
        return eventQueue.take();
    }
}
