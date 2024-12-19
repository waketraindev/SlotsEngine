package wtd.slotsengine.utils.generator;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

/**
 * The BestReelCallback interface is used to define a callback mechanism
 * that provides an action to be executed with a specified return-to-player (RTP)
 * value and a corresponding VirtualReel instance.
 * <p>
 * Implementations of this interface will provide concrete behavior
 * for handling the provided RTP and VirtualReel.
 */
public interface BestReelCallback {
    /**
     * Executes an action with the provided return-to-player (RTP) value and
     * a specified VirtualReel instance.
     *
     * @param rtp  the return-to-player value to be used, typically represented
     *             as a percentage or fractional value.
     * @param reel a VirtualReel instance that represents the virtual reel
     *             configuration to be utilized in the action.
     */
    void run(double rtp, VirtualReel reel);
}