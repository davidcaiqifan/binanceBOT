
import com.binance.api.client.domain.market.AggTrade;
import OlderCode.AggTradesCacheManager;
import OlderCode.Subscriber1;
import OlderCode.PubSubManager;
import Source.MarketDataManager;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC");
        marketDataManager.subscriberOrderBook();
    }
}


