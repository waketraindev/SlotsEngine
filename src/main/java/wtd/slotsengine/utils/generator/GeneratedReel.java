package wtd.slotsengine.utils.generator;

import java.util.Random;
import java.util.stream.IntStream;

public class GeneratedReel {
    private static final int MAX_SYMBOLS = 256;
    private static final int BATCH_SIZE = 8;
    private static final Random random = new Random();
    private static final long[] payoutTable = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 100};
    private final double maxRtp;
    private int index = 0;

    public GeneratedReel(final double targetRtp) {
        this.maxRtp = targetRtp;
    }

    public GeneratedResult generateReel() {
        final int rand10 = boundRand(0), rand9 = boundRand(rand10), rand8 = boundRand(rand9), rand7 = boundRand(rand8),
                rand6 = boundRand(rand7), rand5 = boundRand(rand6), rand4 = boundRand(rand5), rand3 = boundRand(rand4),
                rand2 = boundRand(rand3), rand1 = boundRand(rand2);
        final int reelSize = rand1 + rand2 + rand3 + rand4 + rand5 + rand6 + rand7 + rand8 + rand9 + rand10;
        final byte[] reel = new byte[reelSize];
        fillReel(reel, rand1, rand2, rand3, rand4, rand5, rand6, rand7, rand8, rand9, rand10);
        return adjustReel(reel, reelSize);
    }

    private GeneratedResult adjustReel(byte[] reel, int reelSize) {
        double winAmount = calculateInitialWinAmount(reel);
        if (winAmount == 0) return new GeneratedResult(0.0, reel);
        int zeros = (int) Math.max(0, Math.ceil((winAmount / maxRtp) - reelSize));
        if (zeros == 0) return new GeneratedResult(winAmount / reelSize, reel);
        final byte[] finalReel = createPaddedReel(reel, zeros);
        return new GeneratedResult(winAmount / (reelSize + zeros), finalReel);
    }

    private double calculateInitialWinAmount(byte[] reel) {
        return IntStream.range(0, reel.length).mapToDouble(i -> calculatePayout(reel[i])).parallel().sum();
    }

    private long calculatePayout(final int symbol) {
        return payoutTable[symbol];
    }

    private byte[] createPaddedReel(byte[] reel, int padding) {
        final byte[] finalReel = new byte[padding + reel.length];
        System.arraycopy(reel, 0, finalReel, padding, reel.length);
        return finalReel;
    }

    private void fillReel(byte[] reel, int rand1, int rand2, int rand3, int rand4, int rand5, int rand6, int rand7, int rand8, int rand9, int rand10) {
        addSymbols(reel, (byte) 1, rand1);
        addSymbols(reel, (byte) 2, rand2);
        addSymbols(reel, (byte) 3, rand3);
        addSymbols(reel, (byte) 4, rand4);
        addSymbols(reel, (byte) 5, rand5);
        addSymbols(reel, (byte) 6, rand6);
        addSymbols(reel, (byte) 7, rand7);
        addSymbols(reel, (byte) 8, rand8);
        addSymbols(reel, (byte) 9, rand9);
        addSymbols(reel, (byte) 10, rand10);
    }

    private void addSymbols(final byte[] reel, final byte sym, final int count) {
        final int end = index + count, batch = BATCH_SIZE;
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

    private int boundRand(final int lo) {
        return random.nextInt(lo + 1, lo + MAX_SYMBOLS);
    }
}