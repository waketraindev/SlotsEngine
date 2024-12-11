package wtd.slotsengine.slots.machines.records;

public record SpinOutcome(long betAmount, long winAmount, int symbol, long balance) implements ISpinRecord {
}