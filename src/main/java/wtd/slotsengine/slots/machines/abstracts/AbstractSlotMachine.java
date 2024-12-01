package wtd.slotsengine.slots.machines.abstracts;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractSlotMachine implements SlotMachine {
    private final AtomicLong credits = new AtomicLong(0);
    private final Random rng;

    public AbstractSlotMachine() {
        this.rng = new Random();
    }

    public AbstractSlotMachine(long seed) {
        this.rng = new Random(seed);
    }

    @Override
    public long spin(long betAmount) {
        assertFunds(betAmount, "spin");
        credits.addAndGet(-betAmount);
        return doSpin(betAmount);
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

    public boolean hasCredits(long amount) {
        return getBalance() > amount;
    }

    @Override
    public long getBalance() {
        return credits.get();
    }

    protected void assertFunds(long requiredAmount, String actionName) {
        if (requiredAmount > getBalance())
            throw new InsufficientFundsException("Insufficient credits to %s. Required: %d Have: %d".formatted(actionName, requiredAmount, credits.get()));
    }

    public abstract long doSpin(long betAmount);

    public long winCredits(long winAmount) {
        return credits.addAndGet(winAmount);
    }

    public Random getRandom() {
        return rng;
    }
}
