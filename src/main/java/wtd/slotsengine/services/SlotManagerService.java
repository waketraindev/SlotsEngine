package wtd.slotsengine.services;

import org.springframework.stereotype.Service;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.slots.machines.BasicSlotMachine;

@Service
public class SlotManagerService {
    private final SlotMachine sm;

    public SlotManagerService() {
        sm = new BasicSlotMachine();
    }

    public SlotMachine getSlotMachine() {
        return sm;
    }
}