package wtd.slotsengine.utils.generator;

/**
 * A condition to stop execution based on the number of runs.
 * The condition evaluates whether the current run count is below a specified maximum run count.
 */
public class RunCountCondition implements GenStopCondition {

    private final int maxRunCount;

    /**
     * Constructs a RunCountCondition with a specified maximum run count.
     *
     * @param maxRunCount the maximum number of runs allowed before the condition is no longer met
     */
    public RunCountCondition(int maxRunCount) {
        this.maxRunCount = maxRunCount;
    }

    /**
     * Evaluates whether the specified run count satisfies the condition.
     * This method checks if the provided run count is less than the maximum allowed run count.
     *
     * @param runCount the current number of runs to evaluate
     * @return true if the run count is less than the maximum allowed, false otherwise
     */
    @Override
    public boolean apply(int runCount) {
        return runCount < maxRunCount;
    }
}