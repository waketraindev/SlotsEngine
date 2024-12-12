package wtd.slotsengine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * A utility class that provides helper methods for encoding and decoding operations,
 * including GZIP compression combined with Base64 encoding and decoding.
 * This class is designed for scenarios requiring compact data storage or transmission.
 */
public final class SlotUtils {
    /**
     * Returns the current system time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     *
     * @return the current system time in milliseconds as a {@code Long}
     */
    public static Long now() {
        return System.currentTimeMillis();
    }

    /**
     * Compresses the input data using GZIP and then encodes the compressed data into a Base64 string.
     * This method is useful for reducing data size while converting it into a Base64-encoded format
     * suitable for transmission or storage.
     *
     * @param data the byte array to be compressed and encoded
     * @return a Base64 encoded string representing the GZIP-compressed input byte array
     * @throws RuntimeException if an I/O error occurs during the compression or encoding process
     */
    public static String encodeGzipBase64(byte[] data) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            GZIPOutputStream comp = new GZIPOutputStream(output);
            comp.write(data, 0, data.length);
            comp.close();
            output.close();
            return encodeBase64(output.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes the given byte array as a Base64 encoded string.
     * This method provides a convenient way to convert binary data
     * into a textual representation using Base64 encoding.
     *
     * @param data the byte array to be encoded
     * @return a string representing the Base64 encoded form of the input byte array
     */
    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Decodes a Base64 encoded string, decompresses the decoded binary data using GZIP,
     * and returns the original decompressed byte array.
     * <p>
     * This method is commonly used to reverse compressed and encoded data back to its
     * original byte form, useful for scenarios where data was stored or transmitted
     * in a compact and encoded format.
     *
     * @param dataString the Base64 encoded string representing GZIP-compressed binary data
     * @return a byte array containing the decompressed original data
     * @throws RuntimeException if an I/O error occurs during the decompression process
     */
    public static byte[] decodeGzipBase64(String dataString) {
        try (GZIPInputStream comp = new GZIPInputStream(new ByteArrayInputStream(decodeBase64(dataString)))) {
            return comp.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decodes a Base64 encoded string and returns the original byte array.
     * This method is used to reverse a Base64-encoded textual representation
     * back into its original binary form.
     *
     * @param data the Base64 encoded string to be decoded
     * @return a byte array representing the decoded binary data
     */
    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }
}