package wtd.slotsengine.slots.interfaces;

public interface SlotMachine {
    public void spin(long betAmount);

    public void deposit(long amount);

    public void withdraw(long amount);

    public long getBalance();

}
