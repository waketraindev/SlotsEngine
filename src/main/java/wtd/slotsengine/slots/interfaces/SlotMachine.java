package wtd.slotsengine.slots.interfaces;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.machines.SpinResult;


public interface SlotMachine {

    SpinResult spin(long betAmount) throws InsufficientFundsException;

    long deposit(long amount);

    long withdraw(long amount) throws InsufficientFundsException;

    default boolean hasCredits() {
        return hasCredits(0);
    }

    boolean hasCredits(long amount);

    long getBalance();

    int getResult();

}
