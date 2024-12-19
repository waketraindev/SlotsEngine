package wtd.slotsengine.utils.generator;

import java.util.concurrent.TimeUnit;

/**
 * A stop condition that determines whether an operation should continue
 * running based on a specified time duration. The condition evaluates
 * whether the elapsed execution time has exceeded the defined runtime duration.
 * <p>
 * This class implements the {@link GenStopCondition} interface and provides
 * a mechanism to stop execution after a given amount of time in a specified
 * time unit.
 */
public class TimeStopCondition implements GenStopCondition {
    private static final long startTime = System.currentTimeMillis();
    private final long runtime;

    /**
     * Constructs a {@code TimeStopCondition} with a specified runtime duration.
     *
     * @param amount   the duration of time to run before stopping
     * @param timeUnit the unit of time for the specified amount (e.g., seconds, minutes, hours)
     */
    public TimeStopCondition(int amount, TimeUnit timeUnit) {
        this.runtime = timeUnit.toMillis(amount);
    }

    /**
     * Evaluates whether the operation should continue running based on the elapsed time.
     *
     * @param runCount the number of times the operation has been executed; this parameter is
     *                 currently unused but may be used in future implementations.
     * @return {@code true} if the elapsed time since the start is less than the defined runtime duration,
     * otherwise {@code false}.
     */
    @Override
    public boolean apply(int runCount) {
        return System.currentTimeMillis() - startTime < runtime;
    }
}