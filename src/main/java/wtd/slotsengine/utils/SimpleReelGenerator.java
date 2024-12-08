package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.BasicSlotMachine;
import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.slots.machines.reels.VirtualReelBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SimpleReelGenerator {
    public static final int BOUND = 256;
    private static final Random random = new Random();
    private final double maxRtp;
    private final int historySize;
    private final double[] history;
    private double bestRtp = 0.0;
    private double candidateRtp;
    private VirtualReelBuilder candidateReel;
    private VirtualReel bestReel;

    public SimpleReelGenerator(double maxRtp) {
        this.maxRtp = maxRtp;
        this.historySize = 2048;
        this.history = new double[historySize];
    }

    public static void main(String[] args) {
        double maxRtp = 0.98;
        SimpleReelGenerator gen = new SimpleReelGenerator(maxRtp);
        gen.run(new TimeStopCondition(60, TimeUnit.MINUTES));
    }

    public void run(GenStopCondition stopCondition) {
        for (int runCount = 0; stopCondition.apply(runCount); runCount++) {
            generateReel();
            int index = runCount % historySize;
            if (candidateRtp >= history[index]) {
                if (candidateRtp >= bestRtp) {
                    VirtualReel newReel = candidateReel.sort().build();
                    if (bestReel == null || candidateRtp > bestRtp || newReel.size() < bestReel.size()) {
                        bestRtp = candidateRtp;
                        bestReel = newReel;
                        System.out.printf("Best RTP:\t%.8f:\t/\tSize:\t%d\t/\t%s%n", bestRtp, bestReel.size(), bestReel);
                    }
                }
            }
            history[index] = bestRtp;
        }
    }

    private int boundRand(int lo) {
        return random.nextInt(lo+1, BOUND + lo);
    }

    private void generateReel() {
        final VirtualReelBuilder rb = new VirtualReelBuilder();
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
        rb.addSymbol(1, rand1);
        rb.addSymbol(2, rand2);
        rb.addSymbol(3, rand3);
        rb.addSymbol(4, rand4);
        rb.addSymbol(5, rand5);
        rb.addSymbol(6, rand6);
        rb.addSymbol(7, rand7);
        rb.addSymbol(8, rand8);
        rb.addSymbol(9, rand9);
        rb.addSymbol(10, rand10);
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < rb.size(); i++) {
            winAmount += BasicSlotMachine.calculatePayout(1, rb.get(i));
            cost += 1;
        }
        double rtp = (double) winAmount / cost;
        while (rtp > maxRtp) {
            rb.addSymbol(0, 1);
            cost += 1;
            rtp = (double) winAmount / cost;
        }
        candidateRtp = rtp;
        candidateReel = rb;
    }
}
