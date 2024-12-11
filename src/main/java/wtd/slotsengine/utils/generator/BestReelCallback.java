package wtd.slotsengine.utils.generator;

import wtd.slotsengine.slots.machines.reels.VirtualReel;

public interface BestReelCallback {
    void run(double rtp, VirtualReel reel);
}