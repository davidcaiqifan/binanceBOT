package OlderCode;

public interface Publisher {
    void publish(Event message, PubSubBroker broker);
}
