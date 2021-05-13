package pubsub;

public interface Publisher {
    void publish(Message message, PubSubBroker broker);
}
