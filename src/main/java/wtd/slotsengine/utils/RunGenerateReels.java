package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.utils.generator.ThreadedReelGenerator;
import wtd.slotsengine.utils.generator.TimeStopCondition;

import java.util.concurrent.TimeUnit;

public class RunGenerateReels {
    public static void main(String[] args) {
        ThreadedReelGenerator threadedReelGenerator = new ThreadedReelGenerator(1024, 0.98);
        threadedReelGenerator.run(new TimeStopCondition(1, TimeUnit.MINUTES));

        VirtualReel best = threadedReelGenerator.getBestReel();
        System.out.println("Best: " + best.toString());

    }
}