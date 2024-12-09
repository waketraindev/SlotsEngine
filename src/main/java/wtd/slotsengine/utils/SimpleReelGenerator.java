package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.BasicSlotMachine;
import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SimpleReelGenerator {
    public static final int BOUND = 256;
    private static final Random random = new Random();
    private final double maxRtp;
    private final int historySize;
    private final double[] history;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;

    public SimpleReelGenerator(double maxRtp) {
        this.maxRtp = maxRtp;
        this.historySize = 1024;
        this.history = new double[historySize];
    }

    public static void main(String[] args) {
        double maxRtp = 0.98;
        SimpleReelGenerator gen = new SimpleReelGenerator(maxRtp);
        gen.run(new TimeStopCondition(60, TimeUnit.MINUTES));
    }

    public void run(GenStopCondition stopCondition) {
        for (int runCount = 0; stopCondition.apply(runCount); runCount++) {
            PResult c = generateReel();
            int index = runCount % historySize;
            if (c.rtp >= history[index]) {
                if (bestReel == null || c.rtp > bestRtp || c.rb.length < bestReel.size()) {
                    bestRtp = c.rtp;
                    Arrays.sort(c.rb);
                    bestReel = new VirtualReel(Arrays.stream(c.rb).boxed().toList());
                    System.out.printf("Best RTP:\t%.8f:\t/\tSize:\t%d\t/\t%s%n", bestRtp, bestReel.size(), bestReel);
                }
                history[index] = c.rtp;
            }
        }
    }

    private PResult generateReel() {
        flipIndex = 0;
        final int rand10 = random.nextInt(1, BOUND);
        final int rand9 = boundRand(rand10);
        final int rand8 = boundRand(rand9);
        final int rand7 = boundRand(rand8);
        final int rand6 = boundRand(rand7);
        final int rand5 = boundRand(rand6);
        final int rand4 = boundRand(rand5);
        final int rand3 = boundRand(rand4);
        final int rand2 = boundRand(rand3);
        final int rand1 = boundRand(rand2);
        final int total = rand1 + rand2 + rand3 + rand4 + rand5 + rand6 + rand7 + rand8 + rand9 + rand10;
        final int[] reel = new int[total];
        addSymbol(reel, 1, rand1);
        addSymbol(reel, 2, rand2);
        addSymbol(reel, 3, rand3);
        addSymbol(reel, 4, rand4);
        addSymbol(reel, 5, rand5);
        addSymbol(reel, 6, rand6);
        addSymbol(reel, 7, rand7);
        addSymbol(reel, 8, rand8);
        addSymbol(reel, 9, rand9);
        addSymbol(reel, 10, rand10);
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < total; i++) {
            winAmount += BasicSlotMachine.calculatePayout(1, reel[i]);
            cost += 1;
        }
        double rtp = (double) winAmount / cost;
        int zeros = 0;
        while (rtp > maxRtp) {
            zeros++;
            cost += 1;
            rtp = (double) winAmount / cost;
        }
        final int[] finalReel = new int[zeros + reel.length];
        System.arraycopy(reel, 0, finalReel, zeros, reel.length);
        return new PResult(rtp, finalReel);
    }

    private int flipIndex;

    private void addSymbol(int[] reel, int sym, int count) {
        Arrays.fill(reel, flipIndex, flipIndex + count, sym);
        flipIndex += count;
    }

    private int boundRand(int lo) {
        return random.nextInt(lo + 1, BOUND + lo);
    }

    private record PResult(double rtp, int[] rb) {
    }
}
