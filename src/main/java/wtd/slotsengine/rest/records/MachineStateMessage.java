package wtd.slotsengine.rest.records;

public record MachineStateMessage(long timestampMs, long betAmount, long winAmount, long balance,
                                  int result) {
}
