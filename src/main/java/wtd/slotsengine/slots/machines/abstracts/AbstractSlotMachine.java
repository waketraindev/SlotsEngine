package wtd.slotsengine.slots.machines.abstracts;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSlotMachine implements SlotMachine {
    private final Random rng;
    private final AtomicLong walletBalance = new AtomicLong(0);
    private final AtomicReference<BetResult> lastBet = new AtomicReference<>(null);

    public AbstractSlotMachine() {
        this.rng = new Random();
    }

    public AbstractSlotMachine(long seed) {
        this.rng = new Random(seed);
    }

    @Override
    public BetResult spin(long betAmount) throws InsufficientFundsException {
        assertFunds(betAmount, "spin");
        walletBalance.addAndGet(-betAmount);
        SpinResult result = doSpin(betAmount);
        lastBet.set(new BetResult(result.betAmount(), result.winAmount(), walletBalance.addAndGet(result.winAmount()), result.symbol()));
        return lastBet.get();
    }

    @Override
    public long deposit(long depositAmount) {
        if (depositAmount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        return walletBalance.addAndGet(depositAmount);
    }

    @Override
    public long withdraw(long withdrawAmount) throws InsufficientFundsException {
        if (withdrawAmount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive.");
        assertFunds(withdrawAmount, "withdraw");
        return walletBalance.addAndGet(-withdrawAmount);
    }

    @Override
    public long getBalance() {
        return walletBalance.get();
    }

    protected void assertFunds(long requiredAmount, String actionName) throws InsufficientFundsException {
        if (requiredAmount > getBalance())
            throw new InsufficientFundsException("Insufficient credits to %s. Required: %d Have: %d".formatted(actionName, requiredAmount, walletBalance.get()));
        if (requiredAmount < 0) {
            throw new InsufficientFundsException("Only positive numbers are allowed.");
        }
    }

    protected Random getRandom() {
        return rng;
    }

    protected abstract SpinResult doSpin(long betAmount);
}
