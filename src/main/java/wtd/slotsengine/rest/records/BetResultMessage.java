package wtd.slotsengine.rest.records;

/**
 * Represents a message that contains the result of a bet in the slot engine system.
 * This record encapsulates information such as the timestamp of the bet, the amount bet,
 * the amount won, the resulting balance, and the outcome result code.
 * <p>
 * Fields:
 * - timestampMs: The timestamp in milliseconds of when the bet was processed.
 * - betAmount: The amount wagered in the bet.
 * - winAmount: The amount won as a result of the bet.
 * - balance: The updated balance after the bet is processed.
 * - result: A code indicating the outcome of the bet (e.g., success, failure, specific result states).
 */
public record BetResultMessage(long timestampMs, long betAmount, long winAmount, long balance, int result) {
}