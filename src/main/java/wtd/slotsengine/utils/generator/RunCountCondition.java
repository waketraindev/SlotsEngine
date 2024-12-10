package wtd.slotsengine.utils.generator;

public class RunCountCondition implements GenStopCondition {

    private final int maxRunCount;

    public RunCountCondition(int maxRunCount) {
        this.maxRunCount = maxRunCount;
    }

    @Override
    public boolean apply(int runCount) {
        return runCount < maxRunCount;
    }
}