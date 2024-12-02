package wtd.slotsengine.services;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.rest.exceptions.InvalidSubscriberException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
@EnableScheduling
public class LiveEventsManager {
    private static final Logger log = LoggerFactory.getLogger(LiveEventsManager.class);
    private final List<LiveSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private final Map<UUID, LiveSubscriber> subscriberMap = new ConcurrentHashMap<>();

    public LiveEventsManager() {
        log.info("Live events manager is initializing.");
    }

    @PreDestroy
    public void destroy() {
        subscribers.forEach(sub -> sub.emitter().complete());
    }

    public void subscribe(LiveSubscriber sub) {
        addSubscriber(sub);
        sub.emitter().onCompletion(() -> this.removeSubscriber(sub));
        sub.sendWelcome();
    }

    public void unsubscribe(LiveSubscriber sub) {
        removeSubscriber(sub);
    }

    public void broadcast(SseEmitter.SseEventBuilder message) {
        final Set<ResponseBodyEmitter.DataWithMediaType> bMessage = message.build();
        subscribers.forEach((sub) -> sub.sendEvent(bMessage));
    }

    public void sendDebugText(String text) {
        broadcast(SseEmitter.event().name("DEBUG_TEXT").data(text, MediaType.TEXT_PLAIN));
    }

    public void unsubscribe(UUID uid) throws InvalidSubscriberException {
        removeSubscriber(uid);
    }

    private void addSubscriber(LiveSubscriber sub) {
        subscribers.add(sub);
        subscriberMap.put(sub.getUid(), sub);
        log.info("New subscriber: {}", sub.getUid());
    }

    private void removeSubscriber(UUID uid) throws InvalidSubscriberException {
        LiveSubscriber sub = getSubscriberByUID(uid);
    }

    public LiveSubscriber getSubscriberByUID(UUID uid) throws InvalidSubscriberException {
        LiveSubscriber res = subscriberMap.get(uid);
        if (res == null) {
            throw new InvalidSubscriberException("Invalid subscriber");
        }
        return res;
    }

    private void removeSubscriber(LiveSubscriber sub) {
        subscribers.remove(sub);
        subscriberMap.remove(sub.getUid());
        log.info("Subscriber unsubscribed: {}", sub.getUid());
    }

    @Scheduled(fixedRate = 5000)
    public void pingSubscribersScheduled() {
        pingSubscribers();
    }

    public void pingSubscribers() {
        try {
            log.info("Pinging subscribers: {}", subscribers.size());
            subscribers.forEach(LiveSubscriber::sendPing);
        } catch (Exception e) {
            log.warn("Exception occurred during ping", e);
        }
    }
}
