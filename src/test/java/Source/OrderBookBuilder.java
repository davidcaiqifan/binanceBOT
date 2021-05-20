package Source;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OrderBookBuilder {
    private NavigableMap askEntries;
    private NavigableMap bidEntries;
    
    public OrderBookBuilder() {
        askEntries = new TreeMap<>();
        bidEntries = new TreeMap<>();
    }
    
    public OrderBookBuilder withAsk(BigDecimal price, BigDecimal quantity) {
        askEntries.put(price, quantity);
        return this;
    }

    public OrderBookBuilder withBid(BigDecimal price, BigDecimal quantity) {
        bidEntries.put(price, quantity);
        return this;
    }

    public OrderBook build() {
        OrderBook orderBook = new OrderBook();
        orderBook.put("ASKS", askEntries);
        orderBook.put("BIDS", bidEntries);
        return orderBook;
    }
}
