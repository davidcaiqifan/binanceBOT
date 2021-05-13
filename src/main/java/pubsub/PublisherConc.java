package pubsub;

public class PublisherConc implements Publisher {
    public void publish(Message message, PubSubBroker pubSubBroker) {
        pubSubBroker.addMessage(message);
    }
}
