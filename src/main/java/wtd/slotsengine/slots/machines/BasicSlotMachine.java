package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

final public class BasicSlotMachine implements SlotMachine {
    private final AtomicLong credits = new AtomicLong(0);
    private final Random rng;
    private final List<Integer> reel = new ArrayList<Integer>();

    public BasicSlotMachine() {
        this.rng = new Random();

    }

    public void addSymbol(int sym, int times) {
        for (int i = 0; i < times; i++) {
            reel.add(sym);
        }
    }

    @Override
    public long spin(long betAmount) {
        assertFunds(betAmount, "spin");
        credits.addAndGet(-betAmount);

        int position = rng.nextInt(reel.size());
        long winAmount = spinLogic(position, betAmount);
        if (winAmount > 0) {
            credits.addAndGet(winAmount);
        }
        return winAmount;
    }

    public double testLogic() {
        long cost = 0L;
        long winAmount = 0L;

        for (int i = 0; i < reel.size(); i++) {
            winAmount += spinLogic(i, 1);
            cost += 1;
        }

        return winAmount / (double) cost;
    }

    private long spinLogic(int position, long betAmount) {
        long winAmount = 0L;
        int res = reel.get(position);

        switch (res) {
            case 0 -> winAmount = 0;
            case 1 -> winAmount = betAmount * 1;
            case 2 -> winAmount = betAmount * 2;
            case 3 -> winAmount = betAmount * 3;
            default -> winAmount = 0;
        }

        return winAmount;
    }

    @Override
    public long deposit(long depositAmount) {
        return credits.addAndGet(depositAmount);
    }

    @Override
    public long withdraw(long withdrawAmount) {
        assertFunds(withdrawAmount, "withdraw");
        return credits.addAndGet(-withdrawAmount);
    }

    private void assertFunds(long requiredAmount, String actionName) {
        if (requiredAmount > credits.get())
            throw new InsufficientFundsException("Insufficient credits to %s. Required: %d Have: %d".formatted(actionName, requiredAmount, credits.get()));
    }

    @Override
    public long getBalance() {
        return credits.get();
    }
}
