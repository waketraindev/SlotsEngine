package wtd.slotsengine.rest.exceptions;

public class ExceptionLite extends RuntimeException {
    public ExceptionLite(String message) {
        super(message, null, false, false);
    }
}
