package wtd.slotsengine.rest.records;

public record SpinResultMessage(long timestampMs, long betAmount, long winAmount, long balance,
                                int result) implements IRestMessage {
}
