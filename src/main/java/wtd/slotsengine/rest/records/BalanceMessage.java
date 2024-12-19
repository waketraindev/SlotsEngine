package wtd.slotsengine.rest.records;

/**
 * Represents a balance message in the slot engine system.
 * This record encapsulates the current balance of a player
 * or account, providing a simple mechanism to convey balance
 * information between components.
 */
public record BalanceMessage(Long balance) {
}