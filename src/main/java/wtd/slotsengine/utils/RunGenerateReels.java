package wtd.slotsengine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.utils.generator.ReelOptimizer;
import wtd.slotsengine.utils.generator.TimeStopCondition;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RunGenerateReels {
    private static final Logger log = LoggerFactory.getLogger("ReelOptimizer");

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        log.info("Starting ReelOptimizer");
        ReelOptimizer op = new ReelOptimizer(1024, 0.98);
        op.setFoundBestCallback((rtp, reel) -> {
            log.info("RTP: {} tSize: {} = {}", rtp, reel.size(), reel.toString());
        });
        op.run(new TimeStopCondition(1, TimeUnit.MINUTES));
        VirtualReel best = op.getBestReel();
        long deltaTime = System.currentTimeMillis() - startTime;
        log.info("Optimizer run finished.");
        log.info("Reel: {}", best.toString());
        log.info("RTP: {}", op.getBestRtp());
        log.info("Size: {}", best.size());
        log.info("Elapsed time: {}", Duration.ofMillis(deltaTime));
    }
}