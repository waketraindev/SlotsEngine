package wtd.slotsengine.utils;

import java.util.concurrent.TimeUnit;

public class TimeStopCondition implements GenStopCondition {
    private static final long startTime = System.currentTimeMillis();
    private final long runtime;

    public TimeStopCondition(int amount, TimeUnit timeUnit) {
        this.runtime = timeUnit.toMillis(amount);
    }

    @Override
    public boolean apply(int runCount) {
        return System.currentTimeMillis() - startTime < runtime;
    }
}
