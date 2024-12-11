package wtd.slotsengine.slots.machines.abstracts;

public record SpinOutcome(long betAmount, long winAmount, int symbol, long balance) implements ISpinResult {
}