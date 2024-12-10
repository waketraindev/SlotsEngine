package wtd.slotsengine.utils.generator;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleReelGenerator {
    public static final double TARGET_RTP = 0.98;
    private final ExecutorService workPool = Executors.newWorkStealingPool();
    private final double[] history;
    private final int historySize;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;

    public SimpleReelGenerator(int historySize) {
        this.historySize = historySize;
        this.history = new double[this.historySize];
    }

    public static void main(final String[] args) {
        SimpleReelGenerator gen = new SimpleReelGenerator(1024);
        gen.run(new TimeStopCondition(1, TimeUnit.MINUTES));
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
                    GeneratedReel gen = new GeneratedReel(TARGET_RTP);
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

    private void processFuture(Future<GeneratedResult> future, int runCount) throws InterruptedException, ExecutionException {
        int index = runCount % historySize;
        GeneratedResult candidate = future.get();
        if (candidate.rtp() >= history[index]) {
            if (bestReel == null || candidate.rtp() > bestRtp || candidate.reelBytes().length < bestReel.size()) {
                bestRtp = candidate.rtp();
                bestReel = new VirtualReel(candidate.reelBytes());
                System.out.printf("Best RTP:\t%.8f:\t/\tSize:\t%d\t/\t%s%n", bestRtp, bestReel.size(), bestReel);
            }
            history[index] = candidate.rtp();
        }
    }
}