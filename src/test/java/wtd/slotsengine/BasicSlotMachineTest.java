package wtd.slotsengine;

import org.junit.jupiter.api.Test;
import wtd.slotsengine.slots.machines.BasicSlotMachine;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicSlotMachineTest {

    @Test
    public void testCreation() {
        BasicSlotMachine sm = new BasicSlotMachine();
        assertEquals(0L, sm.getBalance(), "Machine starts with zero balance.");
    }

    @Test
    public void testDeposit() {
        BasicSlotMachine sm = new BasicSlotMachine();
        long funds = new Random().nextLong(9999);
        sm.deposit(funds);
        assertEquals(funds, sm.getBalance(), "Machine deposits funds");
    }
}
