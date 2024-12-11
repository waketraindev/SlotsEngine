package wtd.slotsengine.slots.machines.abstracts;

public interface ISpinResult {
    long betAmount();

    long winAmount();

    int symbol();
}