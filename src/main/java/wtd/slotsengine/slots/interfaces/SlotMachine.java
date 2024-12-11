package wtd.slotsengine.slots.interfaces;

import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.machines.abstracts.SpinOutcome;


public interface SlotMachine {
    SpinOutcome spin(long betAmount) throws InsufficientFundsException;

    long deposit(long amount);

    long withdraw(long amount) throws InsufficientFundsException;

    long getBalance();

    double getMachineRtp();
}