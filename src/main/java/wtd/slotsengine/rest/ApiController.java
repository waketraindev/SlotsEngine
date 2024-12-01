package wtd.slotsengine.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wtd.slotsengine.rest.records.ServerBannerMessage;
import wtd.slotsengine.services.LiveEventsManager;
import wtd.slotsengine.utils.SlotUtils;

@RestController
public class ApiController {
    public static final ServerBannerMessage SERVER_BANNER = new ServerBannerMessage(SlotUtils.PROJECT_VERSION);
    private final LiveEventsManager live;

    public ApiController(LiveEventsManager liveEventsManager) {
        this.live = liveEventsManager;
    }

    @GetMapping("/api")
    public ServerBannerMessage indexAction() {
        return SERVER_BANNER;
    }
}
