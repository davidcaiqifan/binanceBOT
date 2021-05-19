package Analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import Source.OrderBook;

public class SimpleMovingAverage {
    private int period = 0;
    private Long lastOrderBookId = 0L;
    private List<List<Double>> recentPrices;
    private int pointer = 0;
    
    public SimpleMovingAverage(int period) {
        this.period = period;
        recentPrices = new ArrayList<>(Collections.nCopies(period, new ArrayList<>()));
    }
    
    private Double getWeightedMid(OrderBook orderbook) {
        Double bestBidPrice = orderbook.getBestBid().getKey().doubleValue();
        Double bestBidQty = orderbook.getBestBid().getValue().doubleValue();
        Double bestAskPrice = orderbook.getBestAsk().getKey().doubleValue();
        Double bestAskQty = orderbook.getBestAsk().getValue().doubleValue();
        Double weightedMid = (bestBidPrice * bestBidQty + bestAskPrice * bestAskQty) / (bestAskQty + bestBidQty);
        return weightedMid;
    }

    public void updateRecentPrices(NavigableMap<Long, OrderBook> orderBookCache) throws InterruptedException {
        while(orderBookCache.isEmpty()) {
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

    // should return Null if not enough observations
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
    
//
//    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
//        updateRecentPrices();
//        movingAverage = calculateMovingAverage();
//        signalGenerator.generateSignal();
//    }

//    @Override
//    public void run() {
//        try {
//            while (true) {
//                handleEvent((OrderBook) orderBookBroker.get());
//                handleEvent((ScheduleEvent) scheduleQueue.get());
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
