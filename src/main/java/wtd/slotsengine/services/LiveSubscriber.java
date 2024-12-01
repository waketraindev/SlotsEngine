package wtd.slotsengine.services;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.rest.ApiController;
import wtd.slotsengine.rest.records.PingMessage;

import java.util.Set;

import static wtd.slotsengine.utils.SlotUtils.now;

public class LiveSubscriber {
    private final SseEmitter emitter;

    public LiveSubscriber(SseEmitter emitter) {
        this.emitter = emitter;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }

    public void sendWelcome() {
        sendEvent(SseEmitter.event().name("BANNER").data(ApiController.SERVER_BANNER).build());
    }

    public void sendPing() {
        sendEvent(SseEmitter.event().name("PING").data(new PingMessage(now())).build());
    }

    private void sendEvent(Set<ResponseBodyEmitter.DataWithMediaType> event) {
        try {
            emitter.send(event);
        } catch (Exception e) {
        }
    }

}
