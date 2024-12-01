package wtd.slotsengine.rest.records;

import java.util.UUID;

public record ServerBannerMessage(String version, UUID uid) implements IRestMessage {
}
