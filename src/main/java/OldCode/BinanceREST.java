package OldCode;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

public class BinanceREST {
    public BinanceApiRestClient client;
    
    public void connect(String apiKey, String secret) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secret);
        this.client = factory.newRestClient();
    }
    
    public Double getFiveDaySMA(String symbol) {
        return Statistics.getFiveDaySMA(client, symbol);
    }
    
    public Double getTenDaySMA(String symbol) {
        return Statistics.getTenDaySMA(client, symbol);
    }
    
    public String getLatestPrice(String symbol) {
        return Statistics.getLatestPrice(client, symbol);
    }
}
