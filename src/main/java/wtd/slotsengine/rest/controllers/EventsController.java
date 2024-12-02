package wtd.slotsengine.rest.controllers;

import jakarta.annotation.PostConstruct;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Controller for managing server-sent events (SSE) connections and interactions.
 * Provides endpoints to subscribe to event streams and handles specific exceptions related to asynchronous requests.
 */
@RestController
public class EventsController {
    private static final Logger log = LoggerFactory.getLogger(EventsController.class);
    /**
     * An instance of LiveEventsManager that manages live event subscriptions and broadcasts
     * for server-sent events (SSE) within the application.
     * It is responsible for handling the addition and removal of subscribers,
     * as well as managing the delivery of events to subscribers.
     */
    private final LiveEventsManager events;
    private final LiveEventsManager liveEventsManager;

    public EventsController(LiveEventsManager events, LiveEventsManager liveEventsManager) {
        this.events = events;
        this.liveEventsManager = liveEventsManager;
    }

    /**
     * Subscribes a client to server-sent events (SSE) by creating a new {@link SseEmitter}
     * and a {@link LiveSubscriber}. The subscriber is registered to receive events.
     *
     * @param lastEventId an optional header containing the ID of the last event received by the client
     *                    for handling reconnection scenarios. May be null if not provided.
     * @return a new {@link SseEmitter} instance through which events will be sent to the client
     */
    @RequestMapping("/events")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        SseEmitter newEmitter = new SseEmitter();
        LiveSubscriber sub = new LiveSubscriber(newEmitter, UUID.randomUUID());
        events.subscribe(sub);
        return newEmitter;
    }

    @PostConstruct
    void init() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> liveEventsManager.pingSubscribers(), 0, 5, java.util.concurrent.TimeUnit.SECONDS);

    }

    /**
     * Handles exceptions of type AsyncRequestTimeoutException that occur during asynchronous request processing.
     * This method currently ignores these types of exceptions.
     *
     * @param e the AsyncRequestTimeoutException that was thrown
     */
    @SuppressWarnings("EmptyMethod")
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void asyncTimeoutExceptionHandler(AsyncRequestTimeoutException e) {
        //log.warn("Subscriber timed out: " + e.getMessage());
        //At the moment ignore these types of exceptions
    }
}
