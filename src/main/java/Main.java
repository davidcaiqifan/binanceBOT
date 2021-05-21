
import org.quartz.SchedulerException;

import Source.Builder;

public class Main {
    /**
     * Runs BinanceBOT!
     * Takes in symbol, window of first SMA, window of second SMA, and threshold for crossover.
     * @throws SchedulerException
     */
    public static void main(String[] args) throws SchedulerException {
        Builder builder = new Builder("BTCUSDT", 5, 10, 10.0);
        builder.start();
    }
}


