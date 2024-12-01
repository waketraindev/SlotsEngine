package wtd.slotsengine.slots.exceptions;

public class SlotUserException extends RuntimeException {
    public SlotUserException(String message) {
        super(message, null, false, false);
    }
}
