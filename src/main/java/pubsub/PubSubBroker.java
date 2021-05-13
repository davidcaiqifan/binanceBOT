package pubsub;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class PubSubBroker {
    
    private Queue<Message> msgQueue = new LinkedList<Message>();
    private Set<Subscriber> subscriberList = new HashSet<>();

    public void addMessage(Message message) {
        msgQueue.add(message);
    }

    public void addSubscriber(Subscriber subscriber) {
        if (subscriberList.contains(subscriber)) {
            System.out.println("Subscriber already in list.");
        } else {
            subscriberList.add(subscriber);
        }
    }

    public void removeSubscriber(Subscriber subscriber) {
        if (subscriberList.contains(subscriber)) {
            subscriberList.remove(subscriber);
        } else {
            System.out.println("Subscriber does not exist in list");
        }
    }

    public void sendToSubscribers() {
        if (msgQueue.isEmpty()) {
            System.out.println("No messages found");
        } else {
            while(!msgQueue.isEmpty()) {
                Message message = msgQueue.remove();
                for (Subscriber subscriber : subscriberList) {
                    List<Message> subscriberList = subscriber.getSubscriberMsgList();
                    subscriberList.add(message);
                    subscriber.setSubscriberMsgList(subscriberList);
                }
            }
        }
    }
}
