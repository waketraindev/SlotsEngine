package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.utils.generator.ReelOptimizer;
import wtd.slotsengine.utils.generator.TimeStopCondition;

import java.util.concurrent.TimeUnit;

public class RunGenerateReels {
    public static void main(String[] args) {
        ReelOptimizer op = new ReelOptimizer(1024, 0.98);
        op.run(new TimeStopCondition(1, TimeUnit.MINUTES));

        VirtualReel best = op.getBestReel();
        System.out.println("Best: " + best.toString());

    }
}