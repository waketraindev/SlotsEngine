package wtd.slotsengine.slots.interfaces;

public interface SlotMachine {
    long spin(long betAmount);

    long deposit(long amount);

    long withdraw(long amount);

    default boolean hasCredits() {
        return hasCredits(0);
    }

    boolean hasCredits(long amount);

    long getBalance();

}
