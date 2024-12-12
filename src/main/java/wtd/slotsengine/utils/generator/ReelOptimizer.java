package wtd.slotsengine.utils.generator;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code ReelOptimizer} class provides a mechanism for optimizing virtual reels
 * in order to achieve a target Return to Player (RTP) value. It supports both single-threaded
 * and multi-threaded optimization approaches. The optimization process evaluates generated reel
 * configurations and keeps track of the best reel configuration encountered based on RTP and size.
 * <p>
 * The class makes use of a rolling history for evaluation and provides callback support for notifying
 * when a new best configuration is found. It is designed to handle parallel processing using an
 * internal thread pool.
 */
public final class ReelOptimizer {
    private final ExecutorService workPool = Executors.newWorkStealingPool();
    private final double[] history;
    private final int historySize;
    private final double targetRtp;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;
    private BestReelCallback eventNewBest = (rtp, reel) -> {
    };

    /**
     * Constructs a {@code ReelOptimizer} instance with the specified parameters.
     *
     * @param historySize the number of recent results to maintain in history for evaluation
     * @param targetRtp   the target return to player (RTP) value to optimize the reels towards
     */
    public ReelOptimizer(int historySize, double targetRtp) {
        this.historySize = historySize;
        this.history = new double[this.historySize];
        this.targetRtp = targetRtp;
    }

    /**
     * Executes a single-threaded optimization process by iterating reel generation
     * and result processing until the given stopping condition is met.
     *
     * @param stopCondition the condition that dictates when the optimization process
     *                      stops. It evaluates the current run count and determines
     *                      whether to continue or terminate the process.
     */
    @SuppressWarnings("unused")
    public void runSingle(final GenStopCondition stopCondition) {
        int runCount = 0;
        while (stopCondition.apply(runCount)) {
            ReelBufferedGenerator gen = new ReelBufferedGenerator(targetRtp);
            processGeneratedResult(runCount, gen.generateReel());
            runCount++;
        }
    }

    /**
     * Processes a generated result by determining if it represents a new best solution
     * based on the return-to-player (RTP) value and reel size. Updates the history
     * buffer and invokes the callback for the best reel configuration when applicable.
     *
     * @param runCount  the current iteration count, used to determine the index in the history buffer
     * @param candidate the generated result containing RTP value and reel configuration for evaluation
     */
    private void processGeneratedResult(final int runCount, GeneratedResult candidate) {
        int index = runCount % historySize;
        if (candidate.rtp() >= history[index]) {
            if (bestReel == null || candidate.rtp() > bestRtp || candidate.reelBytes().length < bestReel.size()) {
                bestRtp = candidate.rtp();
                bestReel = new VirtualReel(candidate.reelBytes());
                eventNewBest.run(bestRtp, bestReel);
                //System.out.printf("Best RTP:\t%.8f:\t/\tSize:\t%d\t/\t%s%n", bestRtp, bestReel.size(), bestReel);
            }
            history[index] = candidate.rtp();
        }
    }

    /**
     * Executes the multi-threaded optimization and reel generation process
     * until the specified stopping condition is met.
     *
     * @param stopCondition the condition that dictates when the optimization process
     *                      should terminate. It evaluates the current run count
     *                      to determine whether to stop or continue.
     */
    public void run(final GenStopCondition stopCondition) {
        final AtomicInteger runCount = new AtomicInteger(0);
        ArrayBlockingQueue<Future<GeneratedResult>> blockQueue = new ArrayBlockingQueue<>(historySize * 60);
        Thread generatingThread = startGeneratingThread(stopCondition, runCount, blockQueue);
        try {
            while (stopCondition.apply(runCount.get())) {
                Future<GeneratedResult> future = blockQueue.take();
                processFuture(future, runCount.getAndIncrement());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            generatingThread.interrupt();
            workPool.shutdown();
        }
    }

    /**
     * Starts a new generating thread that executes a reel generation process until the specified
     * stopping condition is met. The generated results are submitted to a blocking queue for further
     * processing.
     *
     * @param stopCondition the condition that dictates when the generator should stop creating reels.
     *                      It evaluates the current run count to determine if generation continues.
     * @param runCount      an atomic integer tracking the current iteration count of the generation process.
     * @param blockQueue    a blocking queue used to store {@code Future} results of the generated reels.
     *                      These results are produced by submitting reel generation tasks to a thread pool.
     * @return the {@code Thread} instance responsible for running the reel generation process.
     */
    private Thread startGeneratingThread(GenStopCondition stopCondition, AtomicInteger runCount, ArrayBlockingQueue<Future<GeneratedResult>> blockQueue) {
        Runnable generatingTask = () -> {
            while (stopCondition.apply(runCount.get())) {
                try {
                    ReelBufferedGenerator gen = new ReelBufferedGenerator(targetRtp);
                    blockQueue.put(workPool.submit(gen::generateReel));
                } catch (InterruptedException | RejectedExecutionException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread generatingThread = new Thread(generatingTask);
        generatingThread.setDaemon(true);
        generatingThread.start();
        return generatingThread;
    }

    /**
     * Processes a Future containing a generated result for evaluation and applies it to the optimization process.
     * Retrieves the result from the Future, and passes it along with the current run count
     * to the method responsible for evaluating and updating the best solution.
     *
     * @param future   the Future containing the {@code GeneratedResult} to process. It is expected to hold the
     *                 reel's return-to-player (RTP) value and encoded configuration data.
     * @param runCount the current iteration count during the process of optimization. This value is used
     *                 to manage the history buffer and track progression.
     * @throws InterruptedException if the thread is interrupted while waiting for the Future's result.
     * @throws ExecutionException   if the computation associated with the Future throws an exception.
     */
    private void processFuture(Future<GeneratedResult> future, final int runCount) throws InterruptedException, ExecutionException {
        GeneratedResult candidate = future.get();
        processGeneratedResult(runCount, candidate);
    }

    /**
     * Retrieves the best virtual reel identified during the optimization process.
     * The best reel is determined based on its return-to-player (RTP) value or
     * other optimization criteria such as reel configuration characteristics.
     *
     * @return the VirtualReel instance deemed optimal during the optimization process.
     */
    public VirtualReel getBestReel() {
        return bestReel;
    }

    /**
     * Retrieves the best return-to-player (RTP) value identified during the optimization process.
     * The RTP value represents the highest performance metric achieved for a reel configuration.
     *
     * @return the best RTP value as a double.
     */
    public double getBestRtp() {
        return bestRtp;
    }

    /**
     * Sets a callback function to be invoked whenever a new best virtual reel
     * configuration is identified during the optimization process.
     *
     * @param fn a {@code BestReelCallback} instance that defines the action to perform
     *           with the best return-to-player (RTP) value and its corresponding
     *           {@code VirtualReel} configuration.
     */
    public void setFoundBestCallback(BestReelCallback fn) {
        this.eventNewBest = fn;
    }
}