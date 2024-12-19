package wtd.slotsengine.utils.generator;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * The `ReelBufferedGenerator` class is responsible for generating and adjusting reels for
 * a slot machine system. It creates reels composed of symbols with specific proportions
 * and ensures the generated reels conform to given constraints, including the target
 * RTP (Return to Player).
 */
public class ReelBufferedGenerator {
    private static final int MAX_SYMBOLS = 256;
    private static final int BATCH_SIZE = 8;
    private static final Random random = new Random();
    private static final long[] payoutTable = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 100};
    private final double maxRtp;
    private int index = 0;

    /**
     * Constructs a new instance of the ReelBufferedGenerator class with a specified target RTP (Return to Player).
     *
     * @param targetRtp the target RTP value for the slot machine reel, represented as a double.
     *                  This value defines the maximum RTP that the reel should adhere to.
     */
    public ReelBufferedGenerator(final double targetRtp) {
        this.maxRtp = targetRtp;
    }

    /**
     * Generates a slot machine reel configuration with a random number of symbols
     * for ten different categories. The reel is constructed by generating random
     * counts for each symbol type, filling the reel accordingly, and then adjusting
     * it to meet specific constraints.
     *
     * @return a GeneratedResult object containing the adjusted RTP (Return to Player) value
     * and the reel's byte array configuration.
     */
    public GeneratedResult generateReel() {
        final int rand10 = boundRand(0), rand9 = boundRand(rand10), rand8 = boundRand(rand9), rand7 = boundRand(rand8),
                rand6 = boundRand(rand7), rand5 = boundRand(rand6), rand4 = boundRand(rand5), rand3 = boundRand(rand4),
                rand2 = boundRand(rand3), rand1 = boundRand(rand2);
        final int reelSize = rand1 + rand2 + rand3 + rand4 + rand5 + rand6 + rand7 + rand8 + rand9 + rand10;
        final byte[] reel = new byte[reelSize];
        fillReel(reel, rand1, rand2, rand3, rand4, rand5, rand6, rand7, rand8, rand9, rand10);
        return adjustReel(reel, reelSize);
    }

    /**
     * Adjusts the given reel configuration to meet specific RTP (Return to Player)
     * constraints by calculating the initial win amount, determining if padding is needed,
     * and modifying the reel structure accordingly.
     *
     * @param reel     a byte array representing the reel's initial configuration, where each byte corresponds to a symbol.
     * @param reelSize an integer indicating the size of the reel, representing the number of symbols in the reel.
     * @return a GeneratedResult object containing the adjusted RTP value as a double and
     * a byte array representing the modified reel configuration.
     */
    private GeneratedResult adjustReel(byte[] reel, int reelSize) {
        double winAmount = calculateInitialWinAmount(reel);
        if (winAmount == 0) return new GeneratedResult(0.0, reel);
        int zeros = (int) Math.max(0, Math.ceil((winAmount / maxRtp) - reelSize));
        if (zeros == 0) return new GeneratedResult(winAmount / reelSize, reel);
        final byte[] finalReel = createPaddedReel(reel, zeros);
        return new GeneratedResult(winAmount / (reelSize + zeros), finalReel);
    }

    /**
     * Calculates the initial win amount for the given reel configuration by summing up
     * the payout values for each symbol in the reel.
     *
     * @param reel a byte array representing the reel's configuration, where each byte corresponds to a symbol.
     * @return the initial win amount as a double, calculated by summing the payouts for each symbol in the reel.
     */
    private double calculateInitialWinAmount(byte[] reel) {
        return IntStream.range(0, reel.length).mapToDouble(i -> calculatePayout(reel[i])).parallel().sum();
    }

    /**
     * Calculates the payout for a given symbol based on the predefined payout table.
     *
     * @param symbol the integer value representing the specific symbol for which the payout is calculated.
     * @return the payout amount as a long value corresponding to the given symbol.
     */
    private long calculatePayout(final int symbol) {
        return payoutTable[symbol];
    }

    /**
     * Creates a new byte array with the specified padding added before the original reel data.
     * The padding consists of empty bytes (default initialized to 0), followed by the bytes
     * from the original reel.
     *
     * @param reel    the original byte array representing the reel configuration.
     * @param padding the number of empty bytes to add at the beginning of the new array.
     * @return a new byte array containing the padded reel data.
     */
    private byte[] createPaddedReel(byte[] reel, int padding) {
        final byte[] finalReel = new byte[padding + reel.length];
        System.arraycopy(reel, 0, finalReel, padding, reel.length);
        return finalReel;
    }

    /**
     * Populates the reel with symbols based on the provided random counts for each symbol type.
     * Adds symbols to the reel in the specified quantities for ten different symbol categories.
     *
     * @param reel   the byte array representing the reel to be populated with symbols.
     * @param rand1  the number of occurrences to add for the symbol represented by the byte value 1.
     * @param rand2  the number of occurrences to add for the symbol represented by the byte value 2.
     * @param rand3  the number of occurrences to add for the symbol represented by the byte value 3.
     * @param rand4  the number of occurrences to add for the symbol represented by the byte value 4.
     * @param rand5  the number of occurrences to add for the symbol represented by the byte value 5.
     * @param rand6  the number of occurrences to add for the symbol represented by the byte value 6.
     * @param rand7  the number of occurrences to add for the symbol represented by the byte value 7.
     * @param rand8  the number of occurrences to add for the symbol represented by the byte value 8.
     * @param rand9  the number of occurrences to add for the symbol represented by the byte value 9.
     * @param rand10 the number of occurrences to add for the symbol represented by the byte value 10.
     */
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

    /**
     * Adds a specified number of occurrences of a symbol to a reel.
     *
     * @param reel  the byte array representing the reel to which the symbols will be added.
     * @param sym   the byte value representing the symbol to be added to the reel.
     * @param count the number of occurrences of the symbol to add to the reel.
     */
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

    /**
     * Generates a random integer within a specific range, determined by the provided lower bound and
     * a predefined maximum limit. The range is exclusive of the upper bound.
     *
     * @param lo the lower inclusive bound for the random integer generation.
     * @return a randomly generated integer within the range [lo + 1, lo + MAX_SYMBOLS).
     */
    private int boundRand(final int lo) {
        return random.nextInt(lo + 1, lo + MAX_SYMBOLS);
    }
}