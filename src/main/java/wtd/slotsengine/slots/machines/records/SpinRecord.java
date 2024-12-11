package wtd.slotsengine.slots.machines.records;

public record SpinRecord(long betAmount, long winAmount, int symbol) implements ISpinRecord {
}