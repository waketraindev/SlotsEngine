package wtd.slotsengine.utils.generator;

/**
 * An interface representing a stop condition to determine whether an operation
 * should continue running based on a predefined condition.
 * <p>
 * The implementing classes define specific conditions (e.g., time-based, run count-based)
 * to determine whether the operation should stop or continue executing.
 */
public interface GenStopCondition {
    /**
     * Evaluates whether the stop condition is met based on the given run count.
     *
     * @param runCount the number of times an operation has been executed
     * @return true if the stop condition is met and the operation should stop, false otherwise
     */
    boolean apply(int runCount);
}