package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.rest.exceptions.InvalidSubscriberException;
import wtd.slotsengine.rest.records.ServerVersionMessage;
import wtd.slotsengine.rest.records.SpinResultMessage;
import wtd.slotsengine.services.LiveEventsManager;
import wtd.slotsengine.services.subs.LiveSubscriber;
import wtd.slotsengine.services.SlotManager;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.utils.SlotUtils;

import java.util.UUID;

import static wtd.slotsengine.utils.SlotUtils.now;

@RestController
public class ApiController {
    private static final ServerVersionMessage SERVER_BANNER = new ServerVersionMessage(SlotUtils.PROJECT_VERSION);
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final LiveEventsManager live;
    private final SlotMachine machine;

    public ApiController(LiveEventsManager liveEventsManager, SlotManager slotManager) {
        log.info("API controller is initializing");
        this.live = liveEventsManager;
        this.machine = slotManager.getSlotMachine();
    }

    @GetMapping("/api")
    public ServerVersionMessage indexAction() {
        return SERVER_BANNER;
    }

    @GetMapping("/api/spin")
    public SpinResultMessage spin() {
        long winAmount = machine.spin(1L);
        SpinResultMessage result = new SpinResultMessage(now(), 1L, winAmount, machine.getBalance());
        return result;
    }

    @GetMapping("/api/debugspin")
    public String debugSspin() {
        StringBuilder result = new StringBuilder();
        long winAmount = machine.spin(1);
        result.append("You won ").append(winAmount).append(" credits.").append("\n");
        result.append("Balance: ").append(machine.getBalance()).append("\n");

        SpinResultMessage spinResult = new SpinResultMessage(now(), 1, winAmount, machine.getBalance());
        live.broadcast(SseEmitter.event().name("SPIN_RESULT").data(spinResult));
        return result.toString();
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
