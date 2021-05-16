package OlderCode;

public class PublisherConc implements Publisher {
    public void publish(Event message, PubSubBroker pubSubBroker) {
        pubSubBroker.addMessage(message);
    }
}
