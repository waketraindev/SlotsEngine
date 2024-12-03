package wtd.slotsengine.slots.interfaces;

/**
 * The SlotMachine interface represents a simple slot machine functionality which provides methods
 * for spinning the machine, depositing credits into the machine, withdrawing credits from the machine,
 * and checking the current balance and available credits.
 */
public interface SlotMachine {
    /**
     * Spins the slot machine with a specified bet amount and returns the amount of credits won.
     *
     * @param betAmount the amount of credits to bet on the spin
     * @return the amount of credits won from the spin
     */
    long spin(long betAmount);

    long deposit(long amount);

    long withdraw(long amount);

    default boolean hasCredits() {
        return hasCredits(0);
    }

    boolean hasCredits(long amount);

    long getBalance();

    int getResult();

}
