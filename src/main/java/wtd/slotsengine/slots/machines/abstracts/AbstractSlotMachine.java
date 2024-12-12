package wtd.slotsengine.slots.machines.abstracts;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.slots.machines.records.SpinOutcome;
import wtd.slotsengine.slots.machines.records.SpinRecord;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract base class for implementing slot machine mechanics.
 * <p>
 * This class provides the foundational implementation for a slot machine, including functionality
 * for managing user balances, validating bet amounts, and processing spins. It ensures a consistent
 * structure for extending classes while leaving implementation details for specific slot machines
 * abstract.
 * <p>
 * Features of this class include:
 * - Managing wallet balances with thread-safe operations.
 * - Enforcing rules for betting, depositing, and withdrawing credits.
 * - Utilizing an atomic reference for tracking the last bet outcome.
 * - Abstract definitions for spin logic and machine RTP (Return to Player) calculation.
 * <p>
 * All extending classes must implement specific slot mechanics, such as the spin behavior and
 * calculating the RTP value.
 */
public abstract class AbstractSlotMachine implements SlotMachine {
    private final AtomicLong walletBalance = new AtomicLong(0);
    private final AtomicReference<SpinOutcome> lastBet = new AtomicReference<>(null);

    /**
     * Executes a spin operation in the slot machine, deducting the bet amount from the wallet balance
     * and determining the outcome of the spin.
     *
     * @param betAmount the amount to wager for the spin. Must be positive and less than or equal to
     *                  the current wallet balance.
     * @return a SpinOutcome object representing the result of the spin, including the bet amount,
     * win amount, resulting symbol, and remaining wallet balance.
     * @throws InsufficientFundsException if the bet amount exceeds the available
     *                                    wallet balance or is invalid (e.g., negative).
     */
    @Override
    public SpinOutcome spin(long betAmount) throws InsufficientFundsException {
        assertFunds(betAmount, "spin");
        walletBalance.addAndGet(-betAmount);
        SpinRecord result = doSpin(betAmount);
        lastBet.set(new SpinOutcome(
                result.betAmount(), result.winAmount(), result.symbol(),
                walletBalance.addAndGet(result.winAmount())));
        return lastBet.get();
    }

    /**
     * Deposits the specified amount into the wallet balance of the slot machine.
     * The deposit amount must be a positive value.
     *
     * @param depositAmount the amount to deposit into the wallet balance.
     *                      Must be greater than zero.
     * @return the updated wallet balance after the deposit operation.
     * @throws IllegalArgumentException if the deposit amount is zero or negative.
     */
    @Override
    public long deposit(long depositAmount) {
        if (depositAmount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        return walletBalance.addAndGet(depositAmount);
    }

    /**
     * Withdraws the specified amount from the wallet balance.
     * The withdrawal amount must be a positive value and less than or equal to the current wallet balance.
     *
     * @param withdrawAmount the amount to be withdrawn from the wallet balance. Must be greater than zero and
     *                       not exceed the current wallet balance.
     * @return the updated wallet balance after the withdrawal operation.
     * @throws InsufficientFundsException if the withdrawal amount exceeds the available wallet balance
     *                                    or is invalid (e.g., negative).
     * @throws IllegalArgumentException   if the withdrawal amount is not a positive value.
     */
    @Override
    public long withdraw(long withdrawAmount) throws InsufficientFundsException {
        if (withdrawAmount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive.");
        assertFunds(withdrawAmount, "withdraw");
        return walletBalance.addAndGet(-withdrawAmount);
    }

    /**
     * Retrieves the current balance available in the slot machine's wallet.
     *
     * @return the current wallet balance as a long value.
     */
    @Override
    public long getBalance() {
        return walletBalance.get();
    }

    /**
     * Retrieves the current Return to Player (RTP) value for the slot machine.
     * <p>
     * RTP is a percentage value that indicates the theoretical payout a slot machine
     * is expected to provide over time. It represents the ratio of total winnings
     * to total bets. For example, an RTP of 96.5% means the machine returns 96.5%
     * of the wagered amount on average, over time.
     *
     * @return the machine's RTP value as a double, typically expressed as a percentage.
     */
    public abstract double getMachineRtp();

    /**
     * Validates whether sufficient funds are available in the wallet for the specified action
     * and ensures that the requested amount is positive.
     *
     * @param requiredAmount the amount required to perform the specified action. Must be a positive value
     *                       and less than or equal to the current wallet balance.
     * @param actionName     the name of the action being performed, used for error reporting.
     * @throws InsufficientFundsException if the required amount exceeds the available wallet balance
     *                                    or is a negative value.
     */
    protected void assertFunds(long requiredAmount, String actionName) throws InsufficientFundsException {
        if (requiredAmount > getBalance()) throw new InsufficientFundsException(
                "Insufficient credits to %s. Required: %d Have: %d".formatted(
                        actionName, requiredAmount,
                        walletBalance.get()));
        if (requiredAmount < 0) {
            throw new InsufficientFundsException("Only positive numbers are allowed.");
        }
    }

    /**
     * Executes the core logic of a spin operation in the slot machine and determines the outcome.
     *
     * @param betAmount the amount of credits wagered for the spin. Must be a positive value.
     * @return a SpinRecord containing the details of the spin, including the bet amount, win amount, and resulting symbol.
     */
    protected abstract SpinRecord doSpin(long betAmount);
}