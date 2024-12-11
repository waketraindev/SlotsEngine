package wtd.slotsengine.slots.machines;

import org.junit.jupiter.api.Test;
import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.machines.records.SpinOutcome;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BasicSlotMachineTest {

    public static final Random rng = new Random();

    @Test
    public void testCreation() {
        BasicSlotMachine sm = new BasicSlotMachine();
        assertEquals(0L, sm.getBalance(), "Machine starts with zero balance.");
    }

    @Test
    public void testDeposit() {
        BasicSlotMachine sm = new BasicSlotMachine();
        long funds = rng.nextLong(9999);
        sm.deposit(funds);
        assertEquals(funds, sm.getBalance(), "Machine deposited " + funds);
        assertThrowsExactly(
                IllegalArgumentException.class, () -> sm.deposit(-1000),
                "Machine cannot have negative balance.");
    }

    @Test
    public void testWithdraw() {
        BasicSlotMachine sm = new BasicSlotMachine();
        long funds = new Random().nextLong(9999);
        sm.deposit(funds);

        try {
            sm.withdraw(1L);
            assertEquals(funds - 1L, sm.getBalance(), "Machine has sufficient funds.");
        } catch (InsufficientFundsException e) {
            fail("Machine has no funds. Should not throw exception.");
        }

        assertThrowsExactly(
                InsufficientFundsException.class, () -> sm.withdraw(funds),
                "Machine has insufficient funds.");

        assertDoesNotThrow(() -> sm.withdraw(sm.getBalance()), "Withdraw all funds");
    }

    @Test
    public void testSpin() {
        BasicSlotMachine sm = new BasicSlotMachine();
        sm.deposit(1000);
        assertEquals(1000, sm.getBalance(), "Machine loaded all funds.");

        boolean excepted = false;
        SpinOutcome result = null;
        try {
            result = sm.spin(1);
        } catch (Exception e) {
            excepted = true;
        }

        assertFalse(excepted, "Spin did not throw exceptions");
        assertNotNull(result, "Spin did not return null.");
        assertEquals(1L, result.betAmount(), "Bet amount is correct.");
    }

    @Test
    public void testCalculateRTP() {
        BasicSlotMachine sm = new BasicSlotMachine();
        double rtp = sm.calculateRTP();

        // Assuming the expected RTP based on the demo machine setup
        assertEquals(0.98, rtp, "Calculated RTP is correct.");
    }
}