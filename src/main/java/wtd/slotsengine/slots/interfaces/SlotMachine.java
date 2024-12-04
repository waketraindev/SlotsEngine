package wtd.slotsengine.slots.interfaces;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.machines.abstracts.SpinResult;


public interface SlotMachine {
    SpinResult spin(long betAmount) throws InsufficientFundsException;

    long deposit(long amount);

    long withdraw(long amount) throws InsufficientFundsException;

    long getBalance();
}
