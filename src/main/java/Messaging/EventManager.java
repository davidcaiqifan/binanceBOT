package Messaging;


import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;

public class EventManager {
    private EventBroker<AggTradeEvent> aggTradeBroker = new EventBroker<>();
    private EventBroker<DepthEvent> orderBookBroker = new EventBroker<>();

    public void publish(DepthEvent depthEvent) throws InterruptedException {
        orderBookBroker.addEvent(depthEvent);
    }
    
    public void publish(AggTradeEvent aggTradeEvent) throws InterruptedException {
        aggTradeBroker.addEvent(aggTradeEvent);
    }
}
