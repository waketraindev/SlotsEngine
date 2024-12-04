package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.abstracts.AbstractSlotMachine;
import wtd.slotsengine.slots.machines.utils.VirtualReel;
import wtd.slotsengine.utils.SlotConstants;

/**
 * The BasicSlotMachine class represents a simple slot machine with a single virtual reel.
 * It extends the AbstractSlotMachine to provide a basic implementation of a slot machine
 * with spinning functionality and payout calculation.
 * <p>
 * This class allows users to spin the reel by invoking the doSpin method with a specified
 * bet amount. The result of the spin is based on random selection and predefined payout rules.
 * <p>
 * The class also provides a method to calculate the theoretical Return to Player (RTP)
 * percentage based on the symbols present on the virtual reel.
 */
final public class BasicSlotMachine extends AbstractSlotMachine {
    private final VirtualReel reel;
    private int lastResult;

    /**
     * Constructs a BasicSlotMachine with a default virtual reel configuration.
     * This constructor initializes the slot machine by setting up a virtual reel
     * using predefined symbol data from the `SlotUtils.DEMO_MACHINE` constant.
     */
    public BasicSlotMachine() {
        super();
        reel = new VirtualReel(SlotConstants.DEMO_MACHINE);
    }

    /**
     * Constructs a BasicSlotMachine with the specified virtual reel.
     *
     * @param reel the VirtualReel to be used in this slot machine for spinning and payout calculations.
     *             This parameter provides the symbol data necessary for the slot machine's operations.
     */
    public BasicSlotMachine(final VirtualReel reel) {
        super();
        this.reel = reel;
    }

    /**
     * Executes a spin on the slot machine using the provided bet amount.
     * It determines a random position on the reel, calculates the resulting
     * winning amount based on the position and bet amount, updates the
     * credits if there is a win, and returns the win amount.
     *
     * @param betAmount the amount of credits wagered for this spin.
     * @return the amount of credits won from the spin. Returns zero if there is no win.
     */
    @Override
    public long doSpin(long betAmount) {
        int position = getRandom().nextInt(reel.size());
        return spinLogic(position, betAmount);
    }

    @Override
    public int getResult() {
        return lastResult;
    }

    /**
     * Calculates the win amount based on the result symbol at the specified position of the reel.
     *
     * @param position  the index on the reel indicating the result symbol.
     * @param betAmount the amount of credits wagered for this spin.
     * @return the amount of credits won for the spin, calculated based on the symbol and bet amount.
     * @throws SlotUserException if an invalid symbol is encountered at the given position.
     */
    private long spinLogic(int position, long betAmount) {
        long winAmount;
        int res = reel.get(position);
        this.lastResult = res;

        switch (res) {
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
            default -> throw new SlotUserException("Invalid symbol " + res);
        }

        return winAmount;
    }

    /**
     * Calculates the Return to Player (RTP) of the slot machine. The RTP is determined
     * by simulating full rotations over the virtual reel, where each position on the
     * reel is considered for spin and payout calculation. The method iterates through
     * the entire virtual reel, executing a spin at each position with a bet amount of 1
     * and accumulates both the cost and the win amount associated with these spins.
     *
     * @return the calculated RTP value, which is the total win amount divided by the total cost.
     */
    public double calculateRTP() {
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < reel.size(); i++) {
            winAmount += spinLogic(i, 1);
            cost += 1;
        }
        return winAmount / (double) cost;
    }
}
