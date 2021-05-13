
import com.binance.api.client.domain.market.AggTrade;
import DataManagers.AggTradesManager;
import Strategy1.Subscriber1;
import pubsub.PubSubManager;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        PubSubManager<AggTrade> pbManager = new PubSubManager<>();
        Subscriber1<AggTrade> sub1 = new Subscriber1<>();
        pbManager.addSubscriber(sub1);
        AggTradesManager atManager = new AggTradesManager("ETHBTC", pbManager);
        atManager.startAggTradesEventStream();
        while (true) {
            sub1.printSubscriberMsgList();
            Thread.sleep(5000);
        }
    }
}

