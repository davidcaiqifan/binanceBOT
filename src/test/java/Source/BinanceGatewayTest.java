package Source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;
import com.binance.api.client.domain.market.AggTrade;

public class BinanceGatewayTest {
    private static String symbol = "ETHBTC";

    @Test
    public void initializeCache_success() {
        BinanceGateway binanceGateway = new BinanceGateway(symbol);
        assertTrue(binanceGateway.getAggTradesCache().values().toArray()[0] instanceof AggTrade);
        HashSet<String> stringHashSet = new HashSet<String>();
        stringHashSet.add("ASKS");
        stringHashSet.add("BIDS");
        assertEquals(binanceGateway.getOrderBookCache().getOrderBookCache().keySet(), stringHashSet);
    }
    
    // HOW TO TEST LIVE STREAMING EVENT?
}
