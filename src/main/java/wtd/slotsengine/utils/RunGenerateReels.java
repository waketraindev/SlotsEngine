package wtd.slotsengine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.utils.generator.ReelOptimizer;
import wtd.slotsengine.utils.generator.TimeStopCondition;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * The RunGenerateReels class serves as the main driver for the ReelOptimizer process.
 * It initializes, configures, and executes the ReelOptimizer to determine the optimal
 * virtual reel configuration based on specified conditions.
 * <p>
 * Features of this class include:
 * - Logging the progress and results of the optimization process.
 * - Setting a callback to track and log the best reels during optimization.
 * - Running the optimization process with a specified stop condition (e.g., time limit).
 * - Retrieving and displaying the best reel and associated RTP at the end of the process.
 * <p>
 * This class demonstrates:
 * - Execution of the optimization algorithm.
 * - Usage of time-based stop conditions.
 * - Logging of key metrics such as runtime, reel configuration, and RTP.
 */
public class RunGenerateReels {
    private static final Logger log = LoggerFactory.getLogger("ReelOptimizer");

    /**
     * The main method serves as the entry point for executing the ReelOptimizer process.
     * It initializes and configures the optimization parameters, runs the optimization
     * with a time-based stop condition, and logs the results including the optimal reel
     * configuration, RTP (Return to Player), reel size, and elapsed runtime.
     *
     * @param args Command-line arguments passed to the application. These arguments are
     *             not used in the current implementation.
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        log.info("Starting ReelOptimizer");
        ReelOptimizer op = new ReelOptimizer(1024, 0.98);
        op.setFoundBestCallback((rtp, reel) -> log.info("RTP: {} tSize: {} = {}", rtp, reel.size(), reel));
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