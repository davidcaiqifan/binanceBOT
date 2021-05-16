package OlderCode;

public class Event<T> {
    private T topic;

    public Event(T topic) {
        this.topic = topic;
    }

    public T getTopic() {
        return topic;
    }

    public void setTopic(T topic) {
        this.topic = topic;
    }
}
