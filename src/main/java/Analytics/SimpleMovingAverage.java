package Analytics;

import java.util.NavigableMap;

import Source.OrderBook;

/**
 * Calculates Simple moving average given window.
 */
public class SimpleMovingAverage {
    private int window;
    private Double[] recentPrices;
    private int pointer = 0;

    /**
     * Initializes a SimpleMovingAverage object based on window given.
     */
    public SimpleMovingAverage(int window) {
        this.window = window;
        recentPrices = new Double[window];
    }

    /**
     * Returns the weighted middle of the best bid and best ask in the orderbook.
     */
    protected Double getWeightedMid(OrderBook orderbook) {
        Double bestBidPrice = orderbook.getBestBid().getKey().doubleValue();
        Double bestBidQty = orderbook.getBestBid().getValue().doubleValue();
        Double bestAskPrice = orderbook.getBestAsk().getKey().doubleValue();
        Double bestAskQty = orderbook.getBestAsk().getValue().doubleValue();
        Double weightedMid = (bestBidPrice * bestBidQty + bestAskPrice * bestAskQty) / (bestAskQty + bestBidQty);
        return weightedMid;
    }

    /**
     * Updates recentPrices list with weighted middle prices of new order book.
     * Uses a pointer to keep track of where to write new price into recentPrices list.
     */
    public void updateRecentPrices(NavigableMap<Long, OrderBook> orderBookCache) throws InterruptedException {
        while (orderBookCache.isEmpty()) {
            Thread.sleep(500);
        }
        OrderBook latestOrderBook = orderBookCache.lastEntry().getValue();
        recentPrices[pointer] = getWeightedMid(latestOrderBook);
        if (pointer >= window - 1) {
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
        for (Double price : recentPrices) {
            if (price == null) {
                System.out.println("Not enough data yet!");
                return 0.0;
            }
            totalPrice += price;
            count++;
        }
        Double movingAverage = totalPrice / count;
        System.out.println("SMA" + window + ": " + movingAverage);
        return movingAverage;
    }

    protected Double[] getRecentPrices() {
        return recentPrices;
    }

    protected void setRecentPrice(int index, Double price) {
        recentPrices[index] = price;
    }
}
