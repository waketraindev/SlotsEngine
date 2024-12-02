package wtd.slotsengine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SlotUtilsTest {

    /**
     * Tests for the encodeGzipBase64 method in SlotUtils class.
     * This method compresses the input bytes using GZIP and then encodes the compressed data using Base64.
     */

    @Test
    void testEncodeGzipBase64_withSimpleString() {
        // Initialize the input data.
        String originalString = "Hello, World!";
        byte[] inputData = originalString.getBytes();

        // Encode and decode back to verify.
        String encoded = SlotUtils.encodeGzipBase64(inputData);
        byte[] decoded = SlotUtils.decodeGzipBase64(encoded);

        // Check if the original and decoded data are the same.
        assertEquals(originalString, new String(decoded));
    }

    @Test
    void testEncodeGzipBase64_withEmptyArray() {
        // Initialize an empty input data array.
        byte[] inputData = new byte[0];

        // Encode and decode back to verify.
        String encoded = SlotUtils.encodeGzipBase64(inputData);
        byte[] decoded = SlotUtils.decodeGzipBase64(encoded);

        // The decoded data should be equal to the original empty array.
        assertEquals(inputData.length, decoded.length);
    }

    @Test
    void testEncodeGzipBase64_withLargeData() {
        // Initialize large input data.
        byte[] inputData = "data".repeat(10000).getBytes();

        // Encode and decode back to verify.
        String encoded = SlotUtils.encodeGzipBase64(inputData);
        byte[] decoded = SlotUtils.decodeGzipBase64(encoded);

        // The decoded data should be equal to the original large data.
        assertEquals(new String(inputData), new String(decoded));
    }

    @Test
    void testEncodeGzipBase64_withNullInput() {
        // Expect a RuntimeException when trying to encode a null input.
        assertThrows(RuntimeException.class, () -> SlotUtils.encodeGzipBase64(null));
    }
}