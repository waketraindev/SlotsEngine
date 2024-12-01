package wtd.slotsengine.services;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import wtd.slotsengine.rest.exceptions.AbortedConnectionException;
import wtd.slotsengine.rest.records.PingMessage;
import wtd.slotsengine.rest.records.ServerBannerMessage;
import wtd.slotsengine.utils.SlotUtils;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static wtd.slotsengine.utils.SlotUtils.now;

public class LiveSubscriber {
    private final SseEmitter emitter;
    private final UUID uuid;

    public LiveSubscriber(SseEmitter emitter, UUID uuid) {
        this.emitter = emitter;
        this.uuid = uuid;
    }

    public UUID getUid() {
        return uuid;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }

    public void sendWelcome() {
        sendEvent(SseEmitter.event().name("BANNER").data(new ServerBannerMessage(SlotUtils.PROJECT_VERSION, this.uuid)).build());
    }

    public void sendPing() {
        sendEvent(SseEmitter.event().name("PING").data(new PingMessage(now())).build());
    }

    private void sendEvent(Set<ResponseBodyEmitter.DataWithMediaType> event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            try {
                emitter.completeWithError(e);
            } catch (IllegalStateException aborted) { // Try catch voodoo
                throw new AbortedConnectionException("Failed to send event to subscriber.");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiveSubscriber that)) return false;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
