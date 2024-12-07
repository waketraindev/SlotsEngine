package wtd.slotsengine.utils;

import wtd.slotsengine.slots.machines.BasicSlotMachine;
import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.slots.machines.reels.VirtualReelBuilder;

import java.util.Random;

public class SimpleReelGenerator {
    private static final Random random = new Random();
    private final double maxRtp;
    private final int historySize;
    private final double[] history;
    private double bestRtp = 0.0;
    private VirtualReel bestReel;

    private static class ReelCandidate {
        private final VirtualReelBuilder rb;
        private final Double rtp;

        private ReelCandidate(VirtualReelBuilder rb, double rtp) {
            this.rb = rb;
            this.rtp = rtp;
        }
    }

    public SimpleReelGenerator(double maxRtp) {
        this.maxRtp = maxRtp;
        this.historySize = 512;
        this.history = new double[historySize];
    }

    public static void main(String[] args) {
        double maxRtp = 0.98;
        SimpleReelGenerator gen = new SimpleReelGenerator(maxRtp);
        gen.run(new RunCountCondition(100_000));
    }

    public VirtualReel getBestReel() {
        return this.bestReel;
    }

    /**
     * Executes the reel generation process until a stopping condition is met.
     * The method generates potential candidate reels, calculates their Return to Player (RTP),
     * and keeps track of the best reel generated based on its RTP.
     *
     * @param stopCondition the condition that determines when the reel generation process should stop.
     *                      This is typically implemented to stop after a certain number of iterations or when a specific condition is fulfilled.
     */
    public void run(GenStopCondition stopCondition) {
        for (int runCount = 0; stopCondition.apply(runCount); runCount++) {
            final ReelCandidate candidateReel = generateReel();
            double rtp = candidateReel.rtp;
            int index = runCount % historySize;
            if (rtp >= history[index] && rtp < maxRtp) {
                if (runCount > historySize && rtp > bestRtp) {
                    bestReel = candidateReel.rb.sort().build();
                    bestRtp = rtp;
                    System.out.println("Best RTP: " + bestRtp + ": " + bestReel.toString() + " Size: " + bestReel.size());
                }
                history[index] = rtp;
            }
        }
    }

    private ReelCandidate generateReel() {
        final VirtualReelBuilder rb = new VirtualReelBuilder();
        final int rand10 = 1;
        final int rand9 = random.nextInt(rand10, 64);
        final int rand8 = random.nextInt(rand9, 64);
        final int rand7 = random.nextInt(rand8, 64);
        final int rand6 = random.nextInt(rand7, 64);
        final int rand5 = random.nextInt(rand6, 64);
        final int rand4 = random.nextInt(rand5, 64);
        final int rand3 = random.nextInt(rand4, 64);
        final int rand2 = random.nextInt(rand3, 64);
        final int rand1 = random.nextInt(rand2, 64);
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
        double rtp;
        while ((rtp = BasicSlotMachine.calculateRTP(rb)) >= maxRtp) rb.addSymbol(0, 1);
        return new ReelCandidate(rb, rtp);
    }
}
