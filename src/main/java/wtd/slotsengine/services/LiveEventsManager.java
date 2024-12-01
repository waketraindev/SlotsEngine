package wtd.slotsengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import wtd.slotsengine.rest.exceptions.AbortedConnectionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    public void unsubscribe(UUID uid) {
        removeSubscriber(uid);
    }

    private void addSubscriber(LiveSubscriber sub) {
        subscribers.add(sub);
        subscriberMap.put(sub.getUid(), sub);
        log.info("New subscriber: " + sub.getUid());
    }

    private boolean removeSubscriber(UUID uid) {
        LiveSubscriber sub = subscriberMap.get(uid);
        if (sub != null) {
            removeSubscriber(sub);
            return true;
        }
        return false;
    }

    private void removeSubscriber(LiveSubscriber sub) {
        subscribers.remove(sub);
        subscriberMap.remove(sub.getUid());
        log.info("Subscriber unsubscribed: " + sub.getUid());
    }

    private void pingSubscribers() {
        try {
            log.info("Pinging subscribers: " + subscribers.size());
            subscribers.forEach(LiveSubscriber::sendPing);
        } catch (AbortedConnectionException le) {
            log.warn("Subscriber aborted connection");
        } catch (Exception e) {
            log.warn("Error while pinging subscribers", e);
        }
    }
}
