package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.abstracts.AbstractSlotMachine;
import wtd.slotsengine.slots.machines.records.SpinRecord;
import wtd.slotsengine.slots.machines.reels.IReel;
import wtd.slotsengine.slots.machines.reels.VirtualReel;
import wtd.slotsengine.utils.SlotConstants;

/**
 * Represents a basic slot machine implementation extending {@link AbstractSlotMachine}.
 * This class uses a predefined {@link VirtualReel} for spin operations and provides methods
 * for RTP (Return to Player) calculation and payout computation.
 * <p>
 * The machine operates with fixed paylines and symbol payouts as defined in this class.
 * It also calculates theoretical RTP based on the virtual reel configuration.
 */
final public class BasicSlotMachine extends AbstractSlotMachine {
    private final VirtualReel reel;
    private int counter;
    private Double cachedRtp = 0.0;

    /**
     * Constructs a new instance of the BasicSlotMachine with a predefined virtual reel configuration.
     * <p>
     * This constructor initializes the slot machine by loading a virtual reel from a predefined
     * configuration string stored in {@link SlotConstants#DEMO_MACHINE}. It also calculates and caches
     * the theoretical Return to Player (RTP) value based on the reel's configuration.
     * <p>
     * The virtual reel is shuffled during initialization to ensure randomness, and the resulting
     * instance is ready for spin operations and RTP querying.
     */
    public BasicSlotMachine() {
        super();
        reel = VirtualReel.loadFromString(SlotConstants.DEMO_MACHINE);
        cachedRtp = calculateRTP();
    }

    /**
     * Calculates the theoretical Return to Player (RTP) for the slot machine
     * based on the configuration of the internal virtual reel.
     * <p>
     * The method utilizes a predefined virtual reel configuration to simulate spins
     * and compute the expected payout relative to the total cost. It serves as a
     * wrapper that invokes {@link #calculateRTP(IReel)} using the current reel instance.
     *
     * @return The calculated RTP as a double, representing the payout percentage.
     * For example, an RTP of 0.98 indicates a 98% payout rate.
     */
    public double calculateRTP() {
        return calculateRTP(reel);
    }

    /**
     * Calculates the theoretical Return to Player (RTP) for a given virtual reel configuration.
     * <p>
     * This method simulates spinning through all possible outcomes of the provided reel and
     * computes the RTP as the ratio of the total payout to the total cost over all spins. The RTP
     * is returned as a double value representing the average percentage payout per unit cost.
     *
     * @param reel The virtual reel implementing the {@link IReel} interface, representing the
     *             configuration to calculate the RTP for. Must provide access to its size and individual symbols.
     * @return The calculated RTP as a double, representing the payout rate as a percentage of
     * the total simulated cost. For example, an RTP of 0.98 represents a 98% payout rate.
     */
    public double calculateRTP(IReel reel) {
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < reel.size(); i++) {
            winAmount += calculatePayout(1, reel.get(i));
            cost += 1;
        }
        return winAmount / (double) cost;
    }

    /**
     * Calculates the payout amount based on the bet amount and the resulting symbol.
     * <p>
     * The payout is determined by multiplying the bet amount by a predefined multiplier
     * associated with the provided symbol. If the symbol is invalid, a {@code SlotUserException}
     * is thrown.
     *
     * @param betAmount The amount of credits bet by the user. Must be a positive value.
     * @param symbol    The resulting symbol from the slot machine spin. Defines the payout multiplier.
     *                  Valid values typically range between 0 and 10 inclusive.
     * @return The calculated win amount based on the bet amount and the provided symbol multiplier.
     * @throws SlotUserException if the provided symbol is not within the valid range.
     */
    public long calculatePayout(final long betAmount, final int symbol) {
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

    /**
     * Retrieves the cached theoretical Return to Player (RTP) value for the slot machine.
     * <p>
     * This method returns the precomputed RTP value, which represents the expected payout
     * percentage over time based on the current configuration of the virtual reel. The value
     * is calculated and stored during the initialization of the slot machine to optimize
     * performance for subsequent queries.
     *
     * @return The cached RTP as a double, indicating the theoretical payout percentage.
     * For instance, a returned value of 0.98 corresponds to a 98% payout rate.
     */
    @Override
    public double getMachineRtp() {
        return cachedRtp;
    }

    /**
     * Performs a slot machine spin based on the specified bet amount.
     * <p>
     * This method retrieves the next symbol from the virtual reel and calculates
     * the payout based on the bet amount and the resulting symbol. The spin result
     * includes the bet amount, the calculated win amount, and the resulting symbol.
     *
     * @param betAmount The amount of credits bet by the user. Must be a positive value.
     * @return A {@link SpinRecord} object containing the bet amount, the win amount,
     * and the resulting symbol for the spin.
     * @throws SlotUserException if an invalid symbol is encountered during payout calculation.
     */
    @Override
    public SpinRecord doSpin(final long betAmount) {
        final int res = reel.get(counter++);
        final long winAmount = calculatePayout(betAmount, res);
        return new SpinRecord(betAmount, winAmount, res);
    }
}