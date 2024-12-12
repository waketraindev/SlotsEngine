package wtd.slotsengine.rest.records;

/**
 * Represents a server version message used for communication or identification purposes
 * in the slot engine system. This record contains the version of the server as a string.
 */
public record ServerVersionMessage(String version) {
}