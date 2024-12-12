package wtd.slotsengine.utils.generator;

/**
 * Represents the result of a generated reel configuration, containing its
 * return-to-player (RTP) value and the associated byte data.
 * <p>
 * This record is used as a container for the output of the reel generation
 * process, providing both performance metrics (RTP) and the reel's data
 * representation.
 * <p>
 * Components:
 * - RTP: A double value representing the return-to-player percentage of the
 * generated reel configuration.
 * - reelBytes: A byte array encoding the reel's structure or configuration.
 */
public record GeneratedResult(double rtp, byte[] reelBytes) {
}