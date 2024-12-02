package wtd.slotsengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.rest.exceptions.AbortedConnectionException;
import wtd.slotsengine.rest.exceptions.InvalidSubscriberException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The LiveEventsManager class is responsible for managing live event subscriptions and broadcasting messages
 * to subscribed clients. It schedules regular pings to ensure active connections with subscribers.
 * The class implements the Spring framework interfaces InitializingBean and DisposableBean for lifecycle management.
 */
@Service
public class LiveEventsManager {
    private static final Logger log = LoggerFactory.getLogger(LiveEventsManager.class);
    private final List<LiveSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private final Map<UUID, LiveSubscriber> subscriberMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new instance of LiveEventsManager.
     * <p>
     * Upon initialization, this constructor logs an info message indicating
     * that the Live Events Manager is initializing. It also initializes
     * a single-threaded scheduled executor service which can be used for
     * scheduling and executing tasks asynchronously.
     * <p>
     * The scheduled executor is implemented using `Executors.newSingleThreadScheduledExecutor()`,
     * which ensures that tasks are executed sequentially in a single dedicated thread.
     */
    public LiveEventsManager() {
        log.info("Live events manager is initializing.");
    }


    /**
     * Subscribes a new LiveSubscriber to the event manager. This method adds the subscriber
     * to the internal collections, sets up completion handling to automatically remove the subscriber
     * when the connection ends, and sends a welcome message to the new subscriber.
     *
     * @param sub the LiveSubscriber to be subscribed. The subscriber must have a valid SseEmitter
     *            associated with it for managing server-sent events.
     */
    public void subscribe(LiveSubscriber sub) {
        addSubscriber(sub);
        sub.emitter().onCompletion(() -> this.removeSubscriber(sub));
        sub.sendWelcome();
    }

    /**
     * Unsubscribes an existing LiveSubscriber from the event manager. This method removes the subscriber
     * from the internal collections, ceasing delivery of server-sent events to the specific subscriber.
     *
     * @param sub the LiveSubscriber to be unsubscribed. This should be an existing subscriber with an
     *            active registration in the event manager.
     */
    public void unsubscribe(LiveSubscriber sub) {
        removeSubscriber(sub);
    }

    /**
     * Broadcasts a message to all subscribed clients. This message is constructed using
     * the SseEmitter.SseEventBuilder and delivered to each subscriber via their respective
     * server-sent events (SSE) connection.
     *
     * @param message the SseEventBuilder message to be broadcast to all subscribers. This
     *                must be constructed beforehand and represents the event data to be
     *                sent across the SSE connection.
     */
    public void broadcast(SseEmitter.SseEventBuilder message) {
        final Set<ResponseBodyEmitter.DataWithMediaType> bMessage = message.build();
        subscribers.forEach((sub) -> sub.sendEvent(bMessage));
    }

    /**
     * Sends the provided debug text to all connected clients via server-sent events.
     *
     * @param text the debug text message to be sent to clients. It should be a plain text string that describes
     *             debugging information intended for real-time monitoring or logging in a client application.
     */
    public void sendDebugText(String text) {
        broadcast(SseEmitter.event().name("DEBUG_TEXT").data(text, MediaType.TEXT_PLAIN));
    }

    /**
     * Unsubscribes a subscriber from the service using their unique identifier.
     *
     * @param uid the unique identifier of the subscriber to be unsubscribed
     * @throws InvalidSubscriberException if the provided UID does not correspond
     *                                    to a valid subscriber
     */
    public void unsubscribe(UUID uid) throws InvalidSubscriberException {
        removeSubscriber(uid);
    }

    /**
     * Adds a new subscriber to the list and map of subscribers, and logs the event.
     *
     * @param sub the LiveSubscriber to be added
     */
    private void addSubscriber(LiveSubscriber sub) {
        subscribers.add(sub);
        subscriberMap.put(sub.getUid(), sub);
        log.info("New subscriber: {}", sub.getUid());
    }

    /**
     * Removes a subscriber from the system based on the provided unique identifier.
     *
     * @param uid The unique identifier (UUID) of the subscriber to be removed.
     * @throws InvalidSubscriberException if a subscriber with the given UUID does not exist.
     */
    private void removeSubscriber(UUID uid) throws InvalidSubscriberException {
        LiveSubscriber sub = getSubscriberByUID(uid);
    }

    /**
     * Retrieves a LiveSubscriber from the subscriber map using the specified unique identifier (UID).
     *
     * @param uid the unique identifier of the subscriber to be retrieved
     * @return the LiveSubscriber associated with the provided UID
     * @throws InvalidSubscriberException if the subscriber cannot be found in the map for the given UID
     */
    public LiveSubscriber getSubscriberByUID(UUID uid) throws InvalidSubscriberException {
        LiveSubscriber res = subscriberMap.get(uid);
        if (res == null) {
            throw new InvalidSubscriberException("Invalid subscriber");
        }
        return res;
    }

    /**
     * Removes a subscriber from the list of active subscribers and updates the subscriber map.
     *
     * @param sub the subscriber to be removed
     */
    private void removeSubscriber(LiveSubscriber sub) {
        subscribers.remove(sub);
        subscriberMap.remove(sub.getUid());
        log.info("Subscriber unsubscribed: {}", sub.getUid());
    }

    /**
     * Sends a ping to all current subscribers. The method logs the total number of subscribers
     * being pinged and attempts to send a ping message to each subscriber in the list.
     * <p>
     * If a subscriber terminates the connection abruptly, it catches the
     * AbortedConnectionException and logs a warning message indicating the connection was aborted.
     * <p>
     * If any other type of exception occurs during the process, it catches the exception and
     * logs a warning with the exception details.
     */
    public void pingSubscribers() {
        try {
            log.info("Pinging subscribers: {}", subscribers.size());
            subscribers.forEach(LiveSubscriber::sendPing);
        } catch (AbortedConnectionException le) {
            log.warn("Subscriber aborted connection");
        } catch (Exception e) {
            log.warn("Error while pinging subscribers", e);
        }
    }
}
