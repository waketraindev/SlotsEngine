package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.Random;
import java.util.concurrent.*;

public class SimpleReelGenerator {
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
        try {
            gen.run(new TimeStopCondition(60, TimeUnit.MINUTES));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(final GenStopCondition stopCondition) throws ExecutionException, InterruptedException {
        int runCount = 0;
        @SuppressWarnings("unchecked") Future<PResult>[] ret = new Future[historySize * 10];
        while (stopCondition.apply(runCount)) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = exec.submit(this::generateReel);
            }
            for (Future<PResult> future : ret) {
                int index = runCount % historySize;
                PResult c = (PResult) future.get();
                if (c.rtp >= history[index]) {
                    if (bestReel == null || c.rtp > bestRtp || c.rb.length < bestReel.size()) {
                        bestRtp = c.rtp;
                        bestReel = new VirtualReel(c.rb);
                        System.out.printf(
                                "Best RTP:\t%.8f:\t/\tSize:\t%d\t/\t%s%n", bestRtp, bestReel.size(), bestReel);
                    }
                    history[index] = c.rtp;
                }
                runCount++;
            }
        }
    }

    private PResult generateReel() {
        final int rand10 = boundRand(0), rand9 = boundRand(rand10), rand8 = boundRand(rand9), rand7 = boundRand(rand8),
                rand6 = boundRand(rand7), rand5 = boundRand(rand6), rand4 = boundRand(rand5), rand3 = boundRand(rand4),
                rand2 = boundRand(rand3), rand1 = boundRand(rand2);
        final byte[] reel = new byte[rand1 + rand2 + rand3 + rand4 + rand5 + rand6 + rand7 + rand8 + rand9 + rand10];
        BufferedSymbols fl = new BufferedSymbols();
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
        double cost = 0L, winAmount = 0L;
        for (final int symbol : reel) {
            winAmount += calculatePayout(symbol);
            cost++;
        }
        double rtp = winAmount / cost;
        int zeros = 0;
        while (rtp > maxRtp) {
            zeros++;
            cost++;
            rtp = winAmount / cost;
        }
        final byte[] finalReel = new byte[zeros + reel.length];
        System.arraycopy(reel, 0, finalReel, zeros, reel.length);
        return new PResult(rtp, finalReel);
    }

    private int boundRand(final int lo) {
        return random.nextInt(lo + 1, lo + 256);
    }

    private long calculatePayout(final int symbol) {
        return payoutTable[symbol];
    }

    private record PResult(double rtp, byte[] rb) {
    }

    private static class BufferedSymbols {
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