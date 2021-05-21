
import org.quartz.SchedulerException;

import Source.Builder;

public class Main {
    public static void main(String[] args) throws SchedulerException {
        Builder builder = new Builder("BTCUSDT", 5, 10);
        builder.start();
    }
}


