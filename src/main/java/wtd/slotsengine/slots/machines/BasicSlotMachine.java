package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.abstracts.AbstractSlotMachine;
import wtd.slotsengine.slots.machines.abstracts.SpinResult;
import wtd.slotsengine.slots.machines.abstracts.VirtualReel;
import wtd.slotsengine.utils.SlotConstants;

final public class BasicSlotMachine extends AbstractSlotMachine {
    private final VirtualReel reel;
    private int counter;

    public BasicSlotMachine() {
        super();
        reel = new VirtualReel(SlotConstants.DEMO_MACHINE);
        reel.shuffle();
    }

    @Override
    public SpinResult doSpin(final long betAmount) {
        final int res = reel.get(counter++);
        final long winAmount = calculatePayout(betAmount, res);
        return new SpinResult(betAmount, winAmount, res);
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

    public double calculateRTP() {
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < reel.size(); i++) {
            winAmount += calculatePayout(1, reel.get(i));
            cost += 1;
        }
        return winAmount / (double) cost;
    }
}
