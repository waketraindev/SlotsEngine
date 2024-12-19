package wtd.slotsengine.utils;

import java.util.Random;

public final class SlotConstants {
    /**
     * A constant string representing a compressed and Base64-encoded virtual reel configuration
     * intended for use in slot machine simulations or optimizations. This data can be decoded and
     * decompressed to create a {@code VirtualReel} object.
     * <p>
     * The reel configuration encoded within this string is optimized to a theoretical maximum RTP
     * (Return to Player) of approximately 0.98000000 with a size of 3550 bytes. The encoded data
     * includes reel information required for simulation or game engine processing.
     * <p>
     * This constant serves as a predefined, reusable virtual machine configuration within the
     * slots engine framework.
     */
    public static final String DEMO_MACHINE =
            "H4sIAAAAAAAA/+3BSw4AEAxAwfrT+x/Yzo6km0p4MyIAAAC4L8Aofiq9JzsrjqpBO+obY1HVCdYmCgzeDQAA";
    /**
     * A shared instance of {@link Random} for generating random values across the application.
     * This instance can be used to ensure consistency and avoid the overhead of repeatedly creating new random objects.
     *
     * <p>Primary Use Cases:
     * - To generate random numbers or other randomized data during processing,
     * such as shuffling distributions or producing randomized game outcomes.
     *
     * <p>Example scenarios include:
     * - Shuffling elements in virtual reels.
     * - Generating stochastic behaviors in simulations or gaming logic.
     *
     * <p>Note: The use of a single shared {@code Random} instance ensures thread-safety
     * when accessed in sequential scenarios but may require external synchronization in
     * multi-threaded use cases to prevent contention or unpredictable outcomes.
     */
    public static final Random RANDOM = new Random();
}