package pubsub;

import java.util.Map;

import com.binance.api.client.domain.market.Candlestick;

public class Message {
    private Map<Long, Candlestick> topic;

    public Message(Map<Long, Candlestick> candlesticksCache) {
        this.topic = candlesticksCache;
    }

    public Map<Long, Candlestick> getTopic() {
        return topic;
    }

    public void setTopic(Map<Long, Candlestick> topic) {
        this.topic = topic;
    }
}
