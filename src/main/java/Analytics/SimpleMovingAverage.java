package Analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import Source.OrderBook;

/**
 * Calculates Simple moving average given window.
 */
public class SimpleMovingAverage {
    private int period;
    private Long lastOrderBookId = 0L;
    private List<List<Double>> recentPrices;
    private int pointer = 0;

    /**
     * Initializes a SimpleMovingAverage object based on period given.
     */
    public SimpleMovingAverage(int period) {
        this.period = period;
        recentPrices = new ArrayList<>(Collections.nCopies(period, new ArrayList<>()));
    }

    /**
     * Returns the weighted middle of the best bid and best ask in the orderbook.
     */
    private Double getWeightedMid(OrderBook orderbook) {
        Double bestBidPrice = orderbook.getBestBid().getKey().doubleValue();
        Double bestBidQty = orderbook.getBestBid().getValue().doubleValue();
        Double bestAskPrice = orderbook.getBestAsk().getKey().doubleValue();
        Double bestAskQty = orderbook.getBestAsk().getValue().doubleValue();
        Double weightedMid = (bestBidPrice * bestBidQty + bestAskPrice * bestAskQty) / (bestAskQty + bestBidQty);
        return weightedMid;
    }

    /**
     * Updates recentPrices list with weighted middle prices of new order books.
     * New order books are order books with a orderBookId > lastOrderBookId.
     * Uses a pointer to keep track of where to write new price list into recentPrices list.
     */
    public void updateRecentPrices(NavigableMap<Long, OrderBook> orderBookCache) throws InterruptedException {
        while (orderBookCache.isEmpty()) {
            Thread.sleep(500);
        }
        List<Double> mostRecentPrices = new ArrayList<>();
        Map<Long, OrderBook> recentOrderBooks = orderBookCache.tailMap(lastOrderBookId);
        lastOrderBookId = orderBookCache.lastKey();
        recentOrderBooks.entrySet().forEach(entry -> mostRecentPrices.add(getWeightedMid(entry.getValue())));
        recentPrices.set(pointer, mostRecentPrices);
        if (pointer >= period - 1) {
            pointer = 0;
        } else {
            pointer++;
        }
    }

    /**
     * Calculates average of prices in recentPrices list.
     * Returns zero if there is insufficient data to calculate moving average.
     */
    public Double getMovingAverage() {
        Double totalPrice = 0.0;
        int count = 0;
        for (List<Double> priceList : recentPrices) {
            if (priceList.isEmpty()) {
                System.out.println("Not enough data yet!");
                return 0.0;
            }
            for (Double price : priceList) {
                totalPrice += price;
                count++;
            }
        }
        Double movingAverage = totalPrice / count;
        System.out.println("SMA" + period + ": " + movingAverage);
        return movingAverage;
    }
}
