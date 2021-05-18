package Analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import Messaging.EventBroker;
import Scheduling.ScheduleEvent;
import Source.OrderBook;

public class SimpleMovingAverage extends AnalyticManager {
    private int period = 0;
    private Long lastOrderBookId = 0L;
    private List<List<Double>> recentPrices;
    private int pointer = 0;
    private EventBroker<AggTrade> aggTradesBroker;
    private EventBroker<Source.OrderBook> orderBookBroker;
    private EventBroker<ScheduleEvent> scheduleQueue;

    public SimpleMovingAverage(int period, EventBroker<AggTrade> aggTradesBroker, EventBroker<OrderBook> orderBookBroker, EventBroker<ScheduleEvent> scheduleQueue) {
        this.period = period;
        recentPrices = new ArrayList<>(Collections.nCopies(period, new ArrayList<>()));
        this.aggTradesBroker = aggTradesBroker;
        this.orderBookBroker = orderBookBroker;
        this.scheduleQueue = scheduleQueue;
        
    }
    
    private Double getWeightedMid(OrderBook orderbook) {
        Double bestBidPrice = orderbook.getBestBid().getKey().doubleValue();
        Double bestBidQty = orderbook.getBestBid().getValue().doubleValue();
        Double bestAskPrice = orderbook.getBestAsk().getKey().doubleValue();
        Double bestAskQty = orderbook.getBestAsk().getValue().doubleValue();
        Double weightedMid = (bestBidPrice * bestBidQty + bestAskPrice * bestAskQty) / (bestAskQty + bestBidQty);
        return weightedMid;
    }

    public void updateRecentPrices() throws InterruptedException {
        while(orderBookCache.isEmpty()) {
            System.out.println("test2");
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

    public Double getMovingAverage() {
        Double totalPrice = 0.0;
        int count = 0;
        for (List<Double> priceList : recentPrices) {
            if (priceList.isEmpty()) {
                break;
            }
            for (Double price : priceList) {
                totalPrice += price;
                count++;
            }
        }
        Double movingAverage = totalPrice / count;
        return movingAverage;
    }

    @Override
    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        updateRecentPrices();
        System.out.println(getMovingAverage());
    }

    @Override
    public void run() {
        try {
            while (true) {
                handleEvent((OrderBook) orderBookBroker.get());
                handleEvent((ScheduleEvent) scheduleQueue.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
