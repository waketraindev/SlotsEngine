package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import wtd.slotsengine.rest.exceptions.InvalidSubscriberException;
import wtd.slotsengine.rest.records.ServerVersionMessage;
import wtd.slotsengine.services.LiveEventsManager;
import wtd.slotsengine.services.LiveSubscriber;
import wtd.slotsengine.utils.SlotUtils;

import java.util.UUID;

@RestController
public class ApiController implements InitializingBean {
    private static final ServerVersionMessage SERVER_BANNER = new ServerVersionMessage(SlotUtils.PROJECT_VERSION);
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final LiveEventsManager live;

    public ApiController(LiveEventsManager liveEventsManager) {
        log.info("API controller is initializing");
        this.live = liveEventsManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("API controller is initialized");
    }

    @GetMapping("/api")
    public ServerVersionMessage indexAction() {
        return SERVER_BANNER;
    }

    @GetMapping("/api/{uid}")
    public LiveSubscriber eventsAction(@PathVariable("uid") String userid) {
        try {
            return live.getSubscriberByUID(UUID.fromString(userid));
        } catch (InvalidSubscriberException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
    }
}
