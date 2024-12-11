package wtd.slotsengine.utils.generator;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ReelOptimizer {
    private final ExecutorService workPool = Executors.newWorkStealingPool();
    private final double[] history;
    private final int historySize;
    private final double targetRtp;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;
    private BestReelCallback eventNewBest;

    public ReelOptimizer(int historySize, double targetRtp) {
        this.historySize = historySize;
        this.history = new double[this.historySize];
        this.targetRtp = targetRtp;
    }

    public void runSingle(final GenStopCondition stopCondition) {
        int runCount = 0;
        while (stopCondition.apply(runCount)) {
            GeneratedReel gen = new GeneratedReel(targetRtp);
            processGeneratedResult(runCount, gen.generateReel());
            runCount++;
        }
    }

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

    private Thread startGeneratingThread(GenStopCondition stopCondition, AtomicInteger runCount, ArrayBlockingQueue<Future<GeneratedResult>> blockQueue) {
        Runnable generatingTask = () -> {
            while (stopCondition.apply(runCount.get())) {
                try {
                    GeneratedReel gen = new GeneratedReel(targetRtp);
                    blockQueue.put(workPool.submit(gen::generateReel));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread generatingThread = new Thread(generatingTask);
        generatingThread.setDaemon(true);
        generatingThread.start();
        return generatingThread;
    }

    private void processFuture(Future<GeneratedResult> future, final int runCount) throws InterruptedException, ExecutionException {
        GeneratedResult candidate = future.get();
        processGeneratedResult(runCount, candidate);
    }

    public VirtualReel getBestReel() {
        return bestReel;
    }

    public double getBestRtp() {
        return bestRtp;
    }

    public void setFoundBestCallback(BestReelCallback fn) {
        this.eventNewBest = fn;
    }
}