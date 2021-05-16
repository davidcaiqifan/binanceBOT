package OlderCode;

import OlderCode.PubSubBroker;
import OlderCode.Subscriber;

public class Subscriber1<T> extends Subscriber {
    public void unSubscribe(PubSubBroker broker) {
        broker.removeSubscriber(this);
    }
}
