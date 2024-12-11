package wtd.slotsengine.slots.machines.reels;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VirtualReelBuilderTest {

    private VirtualReelBuilder builder;

    @BeforeEach
    void setUp() {
        builder = VirtualReelBuilder.builder();
    }

    @AfterEach
    void tearDown() {
        builder.build();
        builder = null;
    }

    @Test
    void builder() {
        VirtualReelBuilder l = VirtualReelBuilder.builder();
        Assertions.assertNotNull(l, "Builder should not be null");
    }

    @Test
    void addSymbol() {
        builder.addSymbol((byte) 0, 1);
    }

    @Test
    void size() {
        VirtualReelBuilder b = VirtualReelBuilder.builder().addSymbol((byte) 0, 5);
        int size = b.size();
        Assertions.assertEquals(5, size, "Size should be 1");
    }

    @Test
    void get() {
        VirtualReelBuilder b = VirtualReelBuilder.builder().addSymbol((byte) 0, 1);
        Assertions.assertEquals((byte) 0, b.get(0), "Symbol should be 0");
    }

    @Test
    void sort() {
        VirtualReelBuilder b = VirtualReelBuilder.builder().addSymbol((byte) 1, 1).addSymbol((byte) 0, 1).sort();
        Assertions.assertEquals((byte) 0, b.get(0), "Symbol should be 0");
    }

    @Test
    void shuffle() {
        VirtualReelBuilder b = VirtualReelBuilder.builder().addSymbol((byte) 1, 1).addSymbol((byte) 0, 1).shuffle();
        Assertions.assertNotNull(b, "Builder should not be null");
    }

    @Test
    void build() {
        VirtualReel result = builder.build();
        Assertions.assertNotNull(result, "Result should not be null");
    }
}