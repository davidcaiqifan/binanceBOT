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

    public void clearSubscriberMsgList() { subscriberMsgList.clear(); }

    public void printSubscriberMsgList() {
        for (Message message: subscriberMsgList) {
            System.out.println(message.getTopic());
        }
    }

    public abstract void unSubscribe(PubSubBroker broker);
}
