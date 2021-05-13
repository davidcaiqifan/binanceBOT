
import DataManagers.AggTradesManager;

public class Main {
    public static void main(String[] args) {
        AggTradesManager atManager = new AggTradesManager("ETHBTC");
        atManager.getAggTradesData();
    }
}

