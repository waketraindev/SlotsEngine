package wtd.slotsengine.rest.records;

public record MachineStateMessage(String version, long timestampMs, double rtp, long betAmount, long winAmount,
                                  long balance, int result) {
}
