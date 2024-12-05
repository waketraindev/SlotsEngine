package wtd.slotsengine.utils;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.reels.IReel;
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
        return bestReel;
    }

    public double getBestRtp() {
        return bestRtp;
    }

    public void run(GenStopCondition stopCondition) {
        for (int runCount = 0; stopCondition.apply(runCount); runCount++) {
            VirtualReelBuilder candidateReel = generateReel();
            double rtp = calculateRTP(candidateReel);
            int index = runCount % historySize;
            if (rtp >= history[index] && rtp < maxRtp) {
                if (rtp > bestRtp) {
                    bestReel = candidateReel.build();
                    bestRtp = rtp;
                    System.out.println("Best RTP: " + bestRtp + ": " + bestReel.toString() + " Size: " + bestReel.size());
                }
                history[index] = rtp;
            }
        }
    }

    private VirtualReelBuilder generateReel() {
        VirtualReelBuilder rb = new VirtualReelBuilder();
        int rand10 = random.nextInt(1, 2);
        int rand9 = random.nextInt(rand10, 64);
        int rand8 = random.nextInt(rand9, 64);
        int rand7 = random.nextInt(rand8, 64);
        int rand6 = random.nextInt(rand7, 64);
        int rand5 = random.nextInt(rand6, 64);
        int rand4 = random.nextInt(rand5, 64);
        int rand3 = random.nextInt(rand4, 64);
        int rand2 = random.nextInt(rand3, 64);
        int rand1 = random.nextInt(rand2, 64);
        rb.addSymbol(0, 1);
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
        for (int i = 0; calculateRTP(rb) >= maxRtp; i++) {
            rb.addSymbol(0, 1);
        }
        return rb;
    }

    private double calculateRTP(IReel rb) {
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < rb.size(); i++) {
            winAmount += calculatePayout(1, rb.get(i));
            cost += 1;
        }
        return winAmount / (double) cost;
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
}
