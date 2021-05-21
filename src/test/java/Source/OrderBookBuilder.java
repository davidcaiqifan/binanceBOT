package Source;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OrderBookBuilder {
    private NavigableMap askEntries;
    private NavigableMap bidEntries;

    /**
     * Builds an orderBook to be used in JUnit testing.
     */
    public OrderBookBuilder() {
        askEntries = new TreeMap<>();
        bidEntries = new TreeMap<>();
    }

    /**
     * Adds an ask entry into the orderbook.
     */
    public OrderBookBuilder withAsk(BigDecimal price, BigDecimal quantity) {
        askEntries.put(price, quantity);
        return this;
    }

    /**
     * Adds a bid entry into the orderbook.
     */
    public OrderBookBuilder withBid(BigDecimal price, BigDecimal quantity) {
        bidEntries.put(price, quantity);
        return this;
    }

    /**
     * Builds the orderbook with the ask and bid entries set.
     */
    public OrderBook build() {
        OrderBook orderBook = new OrderBook();
        orderBook.put("ASKS", askEntries);
        orderBook.put("BIDS", bidEntries);
        return orderBook;
    }
}
