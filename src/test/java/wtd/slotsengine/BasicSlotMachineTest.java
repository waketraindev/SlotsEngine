package wtd.slotsengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import wtd.slotsengine.slots.BasicSlotMachine;

import java.util.Random;

public class BasicSlotMachineTest {

    @Test
    public void testCreation() {
        BasicSlotMachine sm = new BasicSlotMachine();
        assertEquals(0L, sm.getBalance(), "Machine starts with zero balance.");
    }

    @Test
    public void testDeposit() {
        BasicSlotMachine sm = new BasicSlotMachine();
        long funds = new Random().nextLong();
        sm.deposit(funds);
        assertEquals(funds, sm.getBalance(), "Machine deposits funds");
    }
}
