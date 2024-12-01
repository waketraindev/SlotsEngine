package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.concurrent.atomic.AtomicLong;

final public class BasicSlotMachine implements SlotMachine {
    private final AtomicLong credits = new AtomicLong(0);

    @Override
    public void spin(long betAmount) {
        assertFunds(betAmount, "spin");
        credits.addAndGet(betAmount);
    }

    @Override
    public void deposit(long depositAmount) {
        credits.addAndGet(depositAmount);
    }

    @Override
    public void withdraw(long withdrawAmount) {
        assertFunds(withdrawAmount, "withdraw");
        credits.addAndGet(-withdrawAmount);
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
