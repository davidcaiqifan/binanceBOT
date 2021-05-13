package OldCode;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Trader {
    private boolean currentPosition; //False when SMA5 < SMA10, True when SMA5 > SMA10
    private BinanceREST binanceRest;
    private String symbol;
    
    public Trader(String symbol) {
        this.symbol = symbol;
        
        this.binanceRest = new BinanceREST();
        binanceRest.connect("API-KEY", "SECRET");
        
        Double currentFiveDaySMA = binanceRest.getFiveDaySMA(symbol);
        Double currentTenDaySMA = binanceRest.getTenDaySMA(symbol);
        this.currentPosition = currentFiveDaySMA > currentTenDaySMA;
    }
    
    public void trade() {
        Double currentFiveDaySMA = binanceRest.getFiveDaySMA(symbol);
        Double currentTenDaySMA = binanceRest.getTenDaySMA(symbol);

        DescriptiveStatistics trades = new DescriptiveStatistics();
        Double currentRatio = Double.parseDouble(binanceRest.getLatestPrice(symbol));
        
        if (currentPosition) {
            if (currentFiveDaySMA <= currentTenDaySMA) {
                trades.addValue(currentRatio);
                System.out.println("Sell at " + currentRatio);
                currentPosition = !currentPosition;
            }
        } else {
            if (currentFiveDaySMA > currentTenDaySMA) {
                trades.addValue(-1 * currentRatio);
                System.out.println("Buy at " + currentRatio);
                currentPosition = !currentPosition;
            }
        }
        
        System.out.println(trades.getSum());
    }
    
}
