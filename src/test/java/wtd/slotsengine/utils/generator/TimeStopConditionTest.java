package wtd.slotsengine.utils.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class TimeStopConditionTest {

    @Test
    void apply() {
        long startTime = System.currentTimeMillis();
        TimeStopCondition cond = new TimeStopCondition(1, TimeUnit.SECONDS);
        int r = 0;
        while (cond.apply(r)) {
            r++;
        }
        long deltaTime = System.currentTimeMillis() - startTime;
        Assertions.assertTrue(deltaTime >= 1000 && deltaTime <= 1010, "Time limit exceeded");
    }
}