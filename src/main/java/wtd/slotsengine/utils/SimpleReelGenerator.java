package wtd.slotsengine.utils;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.reels.VirtualReel;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SimpleReelGenerator {
    public final int BOUND = 256;
    private final Random random = new Random();
    private final double maxRtp;
    private final int historySize;
    private final double[] history;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;
    private int flipIndex;

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
        final int rand10 = random.nextInt(1, BOUND), rand9 = boundRand(rand10), rand8 = boundRand(rand9), rand7 =
                boundRand(rand8), rand6 = boundRand(rand7), rand5 = boundRand(rand6), rand4 = boundRand(rand5), rand3 =
                boundRand(rand4), rand2 = boundRand(rand3), rand1 = boundRand(rand2);
        final int[] reel = new int[rand1 + rand2 + rand3 + rand4 + rand5 + rand6 + rand7 + rand8 + rand9 + rand10];
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
        long cost = 0L, winAmount = 0L;
        for (int j : reel) {
            winAmount += calculatePayout(1, j);
            cost++;
        }
        double rtp = (double) winAmount / cost;
        int zeros = 0;
        while (rtp > maxRtp) {
            zeros++;
            cost++;
            rtp = (double) winAmount / cost;
        }
        final int[] finalReel = new int[zeros + reel.length];
        System.arraycopy(reel, 0, finalReel, zeros, reel.length);
        return new PResult(rtp, finalReel);
    }

    private void addSymbol(int[] reel, int sym, int count) {
        final int end = flipIndex + count, batch = 12;
        for (; flipIndex + batch < end; flipIndex += batch) {
            reel[flipIndex] = sym;
            reel[flipIndex + 1] = sym;
            reel[flipIndex + 2] = sym;
            reel[flipIndex + 3] = sym;
            reel[flipIndex + 4] = sym;
            reel[flipIndex + 5] = sym;
            reel[flipIndex + 6] = sym;
            reel[flipIndex + 7] = sym;
            reel[flipIndex + 8] = sym;
            reel[flipIndex + 9] = sym;
            reel[flipIndex + 10] = sym;
            reel[flipIndex + 11] = sym;
        }
        for (; flipIndex < end; flipIndex++) reel[flipIndex] = sym;
    }

    private int boundRand(int lo) {
        return random.nextInt(lo + 1, BOUND + lo);
    }

    private long calculatePayout(final long betAmount, final int symbol) {
        long winAmount;
        switch (symbol) {
            case 0 -> winAmount = 0;
            case 1 -> winAmount = betAmount;
            case 2 -> winAmount = betAmount * 2;
            case 3 -> winAmount = betAmount * 3;
            case 4 -> winAmount = betAmount * 4;
            case 5 -> winAmount = betAmount * 5;
            case 6 -> winAmount = betAmount * 6;
            case 7 -> winAmount = betAmount * 7;
            case 8 -> winAmount = betAmount * 8;
            case 9 -> winAmount = betAmount * 9;
            case 10 -> winAmount = betAmount * 100;
            default -> throw new SlotUserException("Invalid symbol " + symbol);
        }
        return winAmount;
    }

    private record PResult(double rtp, int[] rb) {
    }
}