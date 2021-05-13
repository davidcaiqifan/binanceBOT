package Strategy1;

import pubsub.PubSubBroker;
import pubsub.Subscriber;

public class Subscriber1<T> extends Subscriber {
    public void unSubscribe(PubSubBroker broker) {
        broker.removeSubscriber(this);
    }
}
