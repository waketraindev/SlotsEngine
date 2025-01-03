package wtd.slotsengine.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

/**
 * A global exception handler for managing exceptions across the whole application.
 * This class uses the @ControllerAdvice annotation to define a centralized exception-handling component.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler(IOException.class)
    public void handleException(Exception ignored, WebRequest req) {
        log.warn("Client connection aborted {}", req.toString());
    }
}