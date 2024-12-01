package wtd.slotsengine.slots;

public class TestApp {
    public static void main(String[] args) {
        BasicSlotMachine sm = new BasicSlotMachine();
        sm.deposit(1);
        sm.spin(5);
    }
}
