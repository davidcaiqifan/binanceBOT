package Messaging;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

public class EventBrokerTest {

    @Test
    public void addEventTest() throws InterruptedException {
        EventBroker<String> eventBroker = new EventBroker<>();
        eventBroker.addEvent("Event1");
        eventBroker.addEvent("Event2");
        BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(2);
        eventQueue.add("Event1");
        eventQueue.add("Event2");
        assertEquals(eventBroker.getEventQueue().toArray(), eventQueue.toArray());
    }

    @Test
    public void getEventTest() throws InterruptedException {
        EventBroker<String> eventBroker = new EventBroker<>();
        eventBroker.addEvent("Event1");
        assertEquals("Event1", eventBroker.get());
    }
}
