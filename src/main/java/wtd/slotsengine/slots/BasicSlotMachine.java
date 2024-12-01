package wtd.slotsengine.slots;

import wtd.slotsengine.slots.interfaces.SlotMachine;

import java.util.concurrent.atomic.AtomicLong;

public class BasicSlotMachine implements SlotMachine {
    private AtomicLong credits = new AtomicLong(0);

    @Override
    public void spin(long betAmount) {
        credits.addAndGet(betAmount);
    }

    @Override
    public void deposit(long amount) {
        credits.addAndGet(amount);
    }

    @Override
    public void withdraw(long amount) {
        credits.addAndGet(-amount);
    }

    @Override
    public long creditBalance() {
        return credits.get();
    }
}
