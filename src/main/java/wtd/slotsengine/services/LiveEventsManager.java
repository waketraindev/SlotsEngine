package wtd.slotsengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.rest.exceptions.AbortedConnectionException;
import wtd.slotsengine.rest.exceptions.InvalidSubscriberException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LiveEventsManager implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LiveEventsManager.class);
    private final ScheduledExecutorService scheduler;
    private final List<LiveSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private final Map<UUID, LiveSubscriber> subscriberMap = new HashMap<>();

    public LiveEventsManager() {
        log.info("Live events manager is initializing.");
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void destroy() throws Exception {
        log.info("Live events manager is shutting down.");
        scheduler.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Live events manager is initialized.");
        scheduler.scheduleAtFixedRate(this::pingSubscribers, 0, 5, TimeUnit.SECONDS);
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

    private void pingSubscribers() {
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
