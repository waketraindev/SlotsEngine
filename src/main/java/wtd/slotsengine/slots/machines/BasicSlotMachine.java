package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.concurrent.atomic.AtomicLong;

final public class BasicSlotMachine implements SlotMachine {
    private final AtomicLong credits = new AtomicLong(0);

    @Override
    public long spin(long betAmount) {
        long winAmount = 0L;
        assertFunds(betAmount, "spin");
        credits.addAndGet(betAmount);
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
