package pubsub;

public class Message<T> {
    private T topic;

    public Message(T topic) {
        this.topic = topic;
    }

    public T getTopic() {
        return topic;
    }

    public void setTopic(T topic) {
        this.topic = topic;
    }
}
