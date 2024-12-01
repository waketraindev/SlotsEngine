package wtd.slotsengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LiveEventsManager {
    private static final Logger log = LoggerFactory.getLogger(LiveEventsManager.class);

    public LiveEventsManager() {
        log.info("Live events manager is initializing.");
    }
}
