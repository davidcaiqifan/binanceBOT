package Analytics;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.Test;
import Source.OrderBook;
import Source.OrderBookBuilder;

public class SimpleMovingAverageTest {

    @Test
    public void getWeightedMidTest() {
        SimpleMovingAverage sma = new SimpleMovingAverage(2);
        OrderBook orderBook = new OrderBookBuilder()
                .withAsk(new BigDecimal(4), new BigDecimal(750))
                .withBid(new BigDecimal(2), new BigDecimal(500))
                .build();
        assertEquals(sma.getWeightedMid(orderBook), Double.valueOf(3.2));
    }

    @Test
    public void updateRecentPricesTest() throws InterruptedException {
        SimpleMovingAverage sma = new SimpleMovingAverage(2);
        OrderBook orderBook1 = new OrderBookBuilder()
                .withAsk(new BigDecimal(5), new BigDecimal(750))
                .withBid(new BigDecimal(2), new BigDecimal(500))
                .build();
        OrderBook orderBook2 = new OrderBookBuilder()
                .withAsk(new BigDecimal(4), new BigDecimal(750))
                .withBid(new BigDecimal(3), new BigDecimal(500))
                .build();
        OrderBook orderBook3 = new OrderBookBuilder()
                .withAsk(new BigDecimal(5), new BigDecimal(250))
                .withBid(new BigDecimal(2), new BigDecimal(500))
                .build();
        NavigableMap<Long, OrderBook> orderBookCache = new TreeMap<>();
        orderBookCache.put(1L, orderBook1);
        sma.updateRecentPrices(orderBookCache);
        orderBookCache.put(2L, orderBook2);
        sma.updateRecentPrices(orderBookCache);
        orderBookCache.put(3L, orderBook3);
        sma.updateRecentPrices(orderBookCache);
        assertEquals(sma.getRecentPrices()[0], Double.valueOf(3));
        assertEquals(sma.getRecentPrices()[1], Double.valueOf(3.6));
    }

    @Test
    public void getMovingAverage_pricesAvail() throws InterruptedException {
        SimpleMovingAverage sma = new SimpleMovingAverage(2);
        sma.setRecentPrice(0, Double.valueOf(1));
        sma.setRecentPrice(1, Double.valueOf(2));
        assertEquals(sma.getMovingAverage(), Double.valueOf(1.5));
    }

    @Test
    public void getMovingAverage_pricesMissing() throws InterruptedException {
        SimpleMovingAverage sma = new SimpleMovingAverage(2);
        sma.setRecentPrice(0, Double.valueOf(1));
        assertEquals(sma.getMovingAverage(), Double.valueOf(0));
    }
    
}
