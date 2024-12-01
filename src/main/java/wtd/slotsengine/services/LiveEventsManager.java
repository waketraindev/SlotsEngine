package wtd.slotsengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import wtd.slotsengine.rest.exceptions.ExceptionLite;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LiveEventsManager implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LiveEventsManager.class);
    private final ScheduledExecutorService scheduler;
    private final List<LiveSubscriber> subscribers = new CopyOnWriteArrayList<>();

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
        subscribers.add(sub);
        sub.getEmitter().onCompletion(() -> subscribers.remove(sub));
        sub.sendWelcome();
    }


    private void noop() {
    }

    private void noop(Throwable throwable) {
    }

    private void pingSubscribers() {
        try {
            log.info("Pinging subscribers: " + subscribers.size());
            subscribers.forEach(LiveSubscriber::sendPing);
        } catch (ExceptionLite le) {
        } catch (Exception e) {
            log.warn("Error while pinging subscribers", e);
        }
    }
}
