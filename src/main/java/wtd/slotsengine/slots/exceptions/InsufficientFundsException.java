package wtd.slotsengine.slots.exceptions;

/**
 * An exception that indicates insufficient funds for a specific operation.
 * <p>
 * This exception is thrown to signal that the requested operation (e.g., spin or withdraw)
 * cannot be completed due to the lack of sufficient funds in the wallet balance or when
 * an invalid amount is provided (e.g., negative value).
 * <p>
 * Typical usage scenarios include:
 * - When a user attempts to place a bet or withdraw an amount that exceeds the current balance.
 * - When an operation requires a positive amount, but a negative or zero value is provided.
 * <p>
 * The exception is used to enforce balance-related constraints and to provide feedback
 * on invalid operations involving financial transactions within a slot machine.
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}