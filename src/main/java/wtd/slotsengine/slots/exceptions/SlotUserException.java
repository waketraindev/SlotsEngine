package wtd.slotsengine.slots.exceptions;

/**
 * Represents a custom runtime exception for slot machine-related operations.
 * <p>
 * This exception is typically thrown when a user-related error occurs during
 * the operation of the slot machine, such as providing an invalid symbol or
 * encountering other user-specific constraints.
 * <p>
 * The exception suppresses stack trace generation and improves performance
 * by disabling writable stack traces and suppression.
 * <p>
 * In the context of the slot machine, this exception can be used to handle
 * invalid operations or inputs gracefully, allowing the application to identify
 * and address specific user-generated errors.
 */
public class SlotUserException extends RuntimeException {
    public SlotUserException(String message) {
        super(message, null, false, false);
    }
}