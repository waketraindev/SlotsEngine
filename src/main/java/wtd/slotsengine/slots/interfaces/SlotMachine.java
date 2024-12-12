package wtd.slotsengine.slots.interfaces;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.machines.records.SpinOutcome;


/**
 * The SlotMachine interface represents the core functionalities of a slot machine system,
 * enabling operations such as placing bets, managing funds, and retrieving key machine metrics.
 */
public interface SlotMachine {
    /**
     * Executes a spin on the slot machine with the specified bet amount.
     * <p>
     * This method deducts the bet amount from the player's balance and performs
     * a spin operation to determine the outcome. The outcome includes the win
     * amount, the resulting symbol, and the updated balance. If the player's
     * current balance is insufficient to cover the bet amount, an
     * {@link InsufficientFundsException} is thrown.
     *
     * @param betAmount the amount of credits to bet on the spin
     * @return the result of the spin, encapsulated in a {@code SpinOutcome} record
     * @throws InsufficientFundsException if the player's balance is insufficient
     *                                    to place the specified bet amount
     */
    SpinOutcome spin(long betAmount) throws InsufficientFundsException;

    /**
     * Deposits the specified amount into the slot machine's balance.
     *
     * @param amount the amount to be deposited, must be a positive value
     * @return the updated balance after the deposit
     */
    long deposit(long amount);

    /**
     * Withdraws a specified amount from the machine's balance.
     * If the balance is insufficient to cover the requested amount,
     * an InsufficientFundsException is thrown.
     *
     * @param amount the amount to withdraw from the machine's balance
     * @return the updated balance of the machine after the withdrawal
     * @throws InsufficientFundsException if the current balance is less than the requested amount to withdraw
     */
    long withdraw(long amount) throws InsufficientFundsException;

    /**
     * Retrieves the current balance available in the slot machine.
     *
     * @return the current balance as a long value
     */
    long getBalance();

    /**
     * Retrieves the RTP (Return to Player) percentage of the slot machine.
     * <p>
     * The RTP represents the expected percentage of wagers that the machine will
     * return to players over time. This value is typically defined by the game's
     * configuration and remains constant for a specific slot machine.
     *
     * @return the RTP value of the slot machine as a double, where the value is
     * expressed as a percentage (e.g., 96.5 for 96.5% RTP).
     */
    double getMachineRtp();
}