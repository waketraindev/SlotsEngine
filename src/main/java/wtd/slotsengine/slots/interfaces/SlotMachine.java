package wtd.slotsengine.slots.interfaces;

public interface SlotMachine {
    public long spin(long betAmount);

    public long deposit(long amount);

    public long withdraw(long amount);

    public long getBalance();

}
