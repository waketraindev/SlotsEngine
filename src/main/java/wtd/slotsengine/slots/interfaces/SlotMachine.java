package wtd.slotsengine.slots.interfaces;

public interface SlotMachine {
    long spin(long betAmount);

    long deposit(long amount);

    long withdraw(long amount);

    long getBalance();

}
