package OlderCode;

import com.binance.api.client.domain.market.AggTrade;

public class PubSubManager<T> {
    private PubSubBroker broker;
    private PublisherConc publisher;

    public PubSubManager() {
        this.broker = new PubSubBroker();
        this.publisher = new PublisherConc();
    }

    public void broadcast(T content) {
        Event<AggTrade> message = new Event(content);
        publisher.publish(message, broker);
        broker.sendToSubscribers();
    }
    
    public void addSubscriber(Subscriber subscriber) {
        broker.addSubscriber(subscriber);
    }
    
    public void removeSubscriber(Subscriber subscriber) {
        broker.removeSubscriber(subscriber);
    }
}
