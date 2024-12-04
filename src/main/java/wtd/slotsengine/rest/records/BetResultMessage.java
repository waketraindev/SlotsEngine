package wtd.slotsengine.rest.records;

public record BetResultMessage(long timestampMs, long betAmount, long winAmount, long balance,
                               int result) {
}
