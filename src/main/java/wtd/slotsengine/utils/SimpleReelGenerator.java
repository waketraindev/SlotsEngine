package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.BasicSlotMachine;
import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.slots.machines.reels.VirtualReelBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SimpleReelGenerator {
    private static final Random random = new Random();
    private final double maxRtp;
    private final int historySize;
    private final double[] history;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;

    private static class ReelCandidate {
        public static VirtualReelBuilder rb;
        public static double rtp;
    }

    public SimpleReelGenerator(double maxRtp) {
        this.maxRtp = maxRtp;
        this.historySize = 1024;
        this.history = new double[historySize];
    }

    public static void main(String[] args) {
        double maxRtp = 0.98;
        SimpleReelGenerator gen = new SimpleReelGenerator(maxRtp);
        gen.run(new TimeStopCondition(2, TimeUnit.MINUTES));
    }

    public VirtualReel getBestReel() {
        return this.bestReel;
    }

    public void run(GenStopCondition stopCondition) {
        for (int runCount = 0; stopCondition.apply(runCount); runCount++) {
            generateReel();
            double rtp = ReelCandidate.rtp;
            int index = runCount % historySize;
            if (rtp >= history[index]) {
                if (rtp >= bestRtp) {
                    bestReel = ReelCandidate.rb.sort().build();
                    System.out.printf("Best RTP: %.8f: %s Size: %d%n", rtp, bestReel.toString(), bestReel.size());
                    bestRtp = rtp;
                }
                history[index] = rtp;
            }
        }
    }

    private void generateReel() {
        final VirtualReelBuilder rb = new VirtualReelBuilder();
        final int rand10 = 1;
        final int rand9 = random.nextInt(rand10, 512);
        final int rand8 = random.nextInt(rand9, 512);
        final int rand7 = random.nextInt(rand8, 512);
        final int rand6 = random.nextInt(rand7, 512);
        final int rand5 = random.nextInt(rand6, 512);
        final int rand4 = random.nextInt(rand5, 512);
        final int rand3 = random.nextInt(rand4, 512);
        final int rand2 = random.nextInt(rand3, 512);
        final int rand1 = random.nextInt(rand2, 512);
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
        double rtp = winAmount / (double) cost;
        if (rtp > maxRtp) {
            while (rtp > maxRtp) {
                rb.addSymbol(0, 1);
                cost += 1;
                rtp = winAmount / (double) cost;
            }
        }
        ReelCandidate.rtp = rtp;
        ReelCandidate.rb = rb;
    }
}
