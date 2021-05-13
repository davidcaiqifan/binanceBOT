package pubsub;

import java.util.ArrayList;
import java.util.List;

public abstract class Subscriber {
    private List<Message> subscriberMsgList = new ArrayList<>();

    public List<Message> getSubscriberMsgList() {
        return subscriberMsgList;
    }

    public void setSubscriberMsgList(List<Message> subscriberMsgList) {
        this.subscriberMsgList = subscriberMsgList;
    }

    public abstract void addSubscriber(PubSubBroker broker);

    public abstract void unSubscribe(PubSubBroker broker);
}
