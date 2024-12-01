package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wtd.slotsengine.rest.records.ServerVersionMessage;
import wtd.slotsengine.services.LiveEventsManager;
import wtd.slotsengine.utils.SlotUtils;

@RestController
public class ApiController implements InitializingBean {
    private static final ServerVersionMessage SERVER_BANNER = new ServerVersionMessage(SlotUtils.PROJECT_VERSION);
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final LiveEventsManager live;

    public ApiController(LiveEventsManager liveEventsManager) {
        log.info("API controller is initializing");
        this.live = liveEventsManager;
    }


    @GetMapping("/api")
    public ServerVersionMessage indexAction() {
        return SERVER_BANNER;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("API controller is initialized");
    }
}
