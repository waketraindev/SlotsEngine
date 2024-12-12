package wtd.slotsengine.services;

import org.springframework.stereotype.Service;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.slots.machines.BasicSlotMachine;

/**
 * The SlotManagerService class is responsible for managing the slot machine service implementation.
 * It initializes and provides access to a SlotMachine instance, specifically a BasicSlotMachine.
 */
@Service
public class SlotManagerService {
    /**
     * Represents the SlotMachine instance managed by the SlotManagerService.
     * This variable is a final reference to the core SlotMachine, which is
     * responsible for executing game mechanics such as spinning, managing
     * balance, and returning statistical data like RTP (Return to Player).
     * The instance is initialized with a specific implementation,
     * such as BasicSlotMachine, to handle slot machine operations.
     */
    private final SlotMachine sm;

    /**
     * SlotManagerService is responsible for managing the slot machine service.
     * It provides an instance of a SlotMachine, specifically a BasicSlotMachine implementation.
     */
    public SlotManagerService() {
        sm = new BasicSlotMachine();
    }

    /**
     * Provides access to the instantiated SlotMachine instance.
     *
     * @return the SlotMachine instance managed by this service
     */
    public SlotMachine getSlotMachine() {
        return sm;
    }
}