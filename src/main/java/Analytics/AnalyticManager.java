package Analytics;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.quartz.SchedulerException;

import Messaging.EventBroker;
import Messaging.EventListener;
import Messaging.EventManager;
import Scheduling.ScheduleEvent;
import Scheduling.ScheduleManager;
import Source.OrderBook;
;
// buffer events here, take in period here
// create sma1 and sma2 nd signal generator here!
// call sma1 to update price when it receives a sma1 timer and same for sma2, then call signal generator to generate signal
public class AnalyticManager implements EventListener, Runnable {
    // protected NavigableMap<Long, AggTrade> aggTradesCache = new TreeMap<>();
    private SimpleMovingAverage sma1;
    private SimpleMovingAverage sma2;
    private EventBroker orderBookBroker;
    private EventBroker scheduleBroker;
    protected NavigableMap<Long, OrderBook> orderBookCache =
            new TreeMap<>();
    private long orderBookId = 0L;
    private SignalGenerator signalGenerator;
    private ScheduleManager scheduleManager;

    public AnalyticManager(int window1, int window2, EventManager eventManager, ScheduleManager scheduleManager) {
        sma1 = new SimpleMovingAverage(window1);
        sma2 = new SimpleMovingAverage(window2);
        orderBookBroker = eventManager.getOrderBookBroker();
        scheduleBroker = eventManager.getScheduleBroker();
        this.scheduleManager = scheduleManager;
        signalGenerator = new SignalGenerator(sma1, sma2, this);
    }
    
    private void initialize() {
        try {
            scheduleManager.periodicCallBack(1000, "sma1");
            scheduleManager.periodicCallBack(1000, "sma2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void handleEvent(OrderBook orderBook) throws InterruptedException {
        orderBookCache.put(orderBookId++, orderBook);
    }

    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        if (timer.getTag().equals("sma1")) {
            sma1.updateRecentPrices(orderBookCache);
        } else if (timer.getTag().equals("sma2")) {
            sma2.updateRecentPrices(orderBookCache);
        }
        signalGenerator.generateSignal();
    }

    @Override
    public void run() {
        initialize();
        while (true) {
            try {
                handleEvent((OrderBook) orderBookBroker.get());
                handleEvent((ScheduleEvent) scheduleBroker.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
//    public void handleEvent(AggTradeEvent aggTradeEvent) throws InterruptedException {
//        Long aggregatedTradeId = aggTradeEvent.getAggregatedTradeId();
//        AggTrade updateAggTrade = (AggTrade) aggTradesCache.get(aggregatedTradeId);
//        if (updateAggTrade == null) {
//            updateAggTrade = new AggTrade();
//        }
//        updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
//        updateAggTrade.setPrice(aggTradeEvent.getPrice());
//        updateAggTrade.setQuantity(aggTradeEvent.getQuantity());
//        updateAggTrade.setFirstBreakdownTradeId(aggTradeEvent.getFirstBreakdownTradeId());
//        updateAggTrade.setLastBreakdownTradeId(aggTradeEvent.getLastBreakdownTradeId());
//        updateAggTrade.setBuyerMaker(aggTradeEvent.isBuyerMaker());
//
//        // Store the updated agg trade in the cache
//        aggTradesCache.put(aggregatedTradeId, updateAggTrade);
//        // System.out.println(updateAggTrade);
//    }
}
