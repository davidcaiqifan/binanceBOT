package OlderCode;

import java.util.ArrayList;
import java.util.List;

public abstract class Subscriber {
    private List<Event> subscriberMsgList = new ArrayList<>();

    public List<Event> getSubscriberMsgList() {
        return subscriberMsgList;
    }

    public void setSubscriberMsgList(List<Event> subscriberMsgList) {
        this.subscriberMsgList = subscriberMsgList;
    }

    public void clearSubscriberMsgList() { subscriberMsgList.clear(); }

    public void printSubscriberMsgList() {
        for (Event message: subscriberMsgList) {
            System.out.println(message.getTopic());
        }
    }

    public abstract void unSubscribe(PubSubBroker broker);
}
