package wtd.slotsengine.slots.machines.abstracts;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSlotMachine implements SlotMachine {
    private final AtomicLong credits = new AtomicLong(0);
    private final Random rng;
    private final AtomicReference<SpinResult> lastSpin = new AtomicReference<>(null);

    public AbstractSlotMachine() {
        this.rng = new Random();
    }

    public AbstractSlotMachine(long seed) {
        this.rng = new Random(seed);
    }

    @Override
    public SpinResult spin(long betAmount) throws InsufficientFundsException {
        assertFunds(betAmount, "spin");
        credits.addAndGet(-betAmount);
        long winAmount = doSpin(betAmount);
        lastSpin.set(new SpinResult(betAmount, winAmount, credits.addAndGet(winAmount), getResult()));
        return lastSpin.get();
    }

    @Override
    public long deposit(long depositAmount) {
        if (depositAmount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        return credits.addAndGet(depositAmount);
    }

    @Override
    public long withdraw(long withdrawAmount) throws InsufficientFundsException {
        if (withdrawAmount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive.");
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

    protected void assertFunds(long requiredAmount, String actionName) throws InsufficientFundsException {
        if (requiredAmount > getBalance())
            throw new InsufficientFundsException("Insufficient credits to %s. Required: %d Have: %d".formatted(actionName, requiredAmount, credits.get()));
        if (requiredAmount < 0) {
            throw new InsufficientFundsException("Only positive numbers are allowed.");
        }
    }

    protected Random getRandom() {
        return rng;
    }

    protected abstract long doSpin(long betAmount);

    protected abstract int getResult();
}
