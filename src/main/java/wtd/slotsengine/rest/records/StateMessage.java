package wtd.slotsengine.rest.records;

/**
 * Represents a state message containing information about the current state
 * of the slot engine. This record combines server version, timestamp,
 * real-time performance metrics, and financial details into a single structure.
 * <p>
 * Fields:
 * - version: The server version identifier in the form of a string.
 * - timestampMs: The timestamp in milliseconds when this state was recorded.
 * - rtp: Real-Time Performance (RTP) metric, typically represented as a ratio or percentage.
 * - betAmount: The total amount bet during the timestamp period.
 * - winAmount: The total amount won during the timestamp period.
 * - balance: The current balance of the player or account.
 * - result: The result code indicative of the state or outcome of the operation.
 */
public record StateMessage(String version, long timestampMs, double rtp, long betAmount, long winAmount, long balance,
                           int result) {
}