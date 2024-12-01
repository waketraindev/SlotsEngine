package wtd.slotsengine.rest.records;

public record ServerBannerMessage(String version) implements IRestMessage {
    public ServerBannerMessage(String version) {
        this.version = version;
    }
}
