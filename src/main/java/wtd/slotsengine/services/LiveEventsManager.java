package wtd.slotsengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class LiveEventsManager implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LiveEventsManager.class);
    private final ScheduledExecutorService scheduler;

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
    }
}
