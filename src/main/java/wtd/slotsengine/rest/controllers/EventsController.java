package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.services.LiveEventsManager;
import wtd.slotsengine.services.LiveSubscriber;

import java.util.UUID;

@RestController
public class EventsController {
    private static final Logger log = LoggerFactory.getLogger(EventsController.class);
    private final LiveEventsManager events;

    public EventsController(LiveEventsManager events) {
        this.events = events;
    }

    @RequestMapping("/events")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        SseEmitter newEmitter = new SseEmitter();
        LiveSubscriber sub = new LiveSubscriber(newEmitter, UUID.randomUUID());
        events.subscribe(sub);
        return newEmitter;
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void asyncTimeoutExceptionHandler(AsyncRequestTimeoutException e) {
        //log.warn("Subscriber timed out: " + e.getMessage());
        //At the moment ignore these types of exceptions
    }
}
