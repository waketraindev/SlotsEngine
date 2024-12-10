package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleReelGenerator {
    private final Random random = new Random();
    private final ExecutorService exec = Executors.newWorkStealingPool();
    private final double maxRtp;
    private final double[] history;
    private final int historySize;
    private final long[] payoutTable = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 100};
    private double bestRtp = 0.0;
    private VirtualReel bestReel;

    public SimpleReelGenerator(final double maxRtp) {
        this.maxRtp = maxRtp;
        this.historySize = 1024;
        this.history = new double[historySize];
    }

    public static void main(final String[] args) {
        final double maxRtp = 0.98;
        SimpleReelGenerator gen = new SimpleReelGenerator(maxRtp);
        gen.run(new TimeStopCondition(1, TimeUnit.MINUTES));
    }

    public void run(final GenStopCondition stopCondition) {
        final AtomicInteger runCount = new AtomicInteger(0);
        ArrayBlockingQueue<Future<GeneratedResult>> blockQueue = new ArrayBlockingQueue<>(historySize * 1024);
        Runnable generatingTask = () -> {
            while (stopCondition.apply(runCount.get())) {
                try {
                    blockQueue.put(exec.submit(this::generateReel));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread generatingThread = new Thread(generatingTask);
        generatingThread.start();

        try {
            while (stopCondition.apply(runCount.get())) {
                Future<GeneratedResult> future = blockQueue.take();
                processFuture(future, runCount.getAndIncrement());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            generatingThread.interrupt();
            exec.shutdown();
        }
    }

    private void processFuture(Future<GeneratedResult> future, int runCount) throws InterruptedException, ExecutionException {
        int index = runCount % historySize;
        GeneratedResult candidate = future.get();
        if (candidate.rtp >= history[index]) {
            if (bestReel == null || candidate.rtp > bestRtp || candidate.reelBytes.length < bestReel.size()) {
                bestRtp = candidate.rtp;
                bestReel = new VirtualReel(candidate.reelBytes);
                System.out.printf("Best RTP:\t%.8f:\t/\tSize:\t%d\t/\t%s%n", bestRtp, bestReel.size(), bestReel);
            }
            history[index] = candidate.rtp;
        }
    }

    private GeneratedResult generateReel() {
        final int rand10 = boundRand(0), rand9 = boundRand(rand10), rand8 = boundRand(rand9), rand7 = boundRand(rand8),
                rand6 = boundRand(rand7), rand5 = boundRand(rand6), rand4 = boundRand(rand5), rand3 = boundRand(rand4),
                rand2 = boundRand(rand3), rand1 = boundRand(rand2);
        final int reelSize = rand1 + rand2 + rand3 + rand4 + rand5 + rand6 + rand7 + rand8 + rand9 + rand10;
        final byte[] reel = new byte[reelSize];
        BufferedSymbolWriter fl = new BufferedSymbolWriter();
        fl.add(reel, (byte) 1, rand1);
        fl.add(reel, (byte) 2, rand2);
        fl.add(reel, (byte) 3, rand3);
        fl.add(reel, (byte) 4, rand4);
        fl.add(reel, (byte) 5, rand5);
        fl.add(reel, (byte) 6, rand6);
        fl.add(reel, (byte) 7, rand7);
        fl.add(reel, (byte) 8, rand8);
        fl.add(reel, (byte) 9, rand9);
        fl.add(reel, (byte) 10, rand10);
        double winAmount = 0L;
        for (final int symbol : reel)
            winAmount += calculatePayout(symbol);

        double rtp = winAmount / reelSize;
        int zeros = 0;
        while (rtp > maxRtp) {
            zeros++;
            rtp = winAmount / (reelSize + zeros);
        }
        final byte[] finalReel = new byte[zeros + reel.length];
        System.arraycopy(reel, 0, finalReel, zeros, reel.length);
        return new GeneratedResult(rtp, finalReel);
    }

    private int boundRand(final int lo) {
        return random.nextInt(lo + 1, lo + 256);
    }

    private long calculatePayout(final int symbol) {
        return payoutTable[symbol];
    }

    private record GeneratedResult(double rtp, byte[] reelBytes) {
    }

    private final static class BufferedSymbolWriter {
        private int index = 0;

        private void add(final byte[] reel, final byte sym, final int count) {
            final int end = index + count, batch = 8;
            for (; index + batch < end; index += batch) {
                reel[index] = sym;
                reel[index + 1] = sym;
                reel[index + 2] = sym;
                reel[index + 3] = sym;
                reel[index + 4] = sym;
                reel[index + 5] = sym;
                reel[index + 6] = sym;
                reel[index + 7] = sym;
            }
            for (; index < end; index++) reel[index] = sym;
        }
    }
}