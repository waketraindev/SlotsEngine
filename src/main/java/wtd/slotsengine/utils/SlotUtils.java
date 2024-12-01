package wtd.slotsengine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Utility class providing methods for working with binary data,
 * including encoding and decoding using Base64 and GZIP compression.
 */
public class SlotUtils {
    public static final String PROJECT_VERSION = "0.0.2";

    public static Long now() {
        return System.currentTimeMillis();
    }

    /**
     * Compresses the input byte array using GZIP compression and encodes the
     * result into a Base64 string.
     *
     * @param data the input byte array to be compressed and encoded
     * @return a Base64 encoded string representing the compressed input data
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
     * Encodes the given byte array into a Base64 encoded string.
     *
     * @param data the byte array to be encoded
     * @return a Base64 encoded string representing the input byte array
     */
    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Decodes a Base64 encoded string into a byte array.
     *
     * @param data the Base64 encoded string to be decoded
     * @return a byte array representing the decoded data
     */
    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * Decodes a GZIP compressed and Base64 encoded string and returns the
     * original byte array.
     *
     * @param dataString the Base64 encoded string containing GZIP compressed data
     * @return a byte array representing the decompressed original data
     * @throws RuntimeException if an I/O error occurs during the decompression process
     */
    public static byte[] decodeGzipBase64(String dataString) {
        try (GZIPInputStream comp = new GZIPInputStream(new ByteArrayInputStream(decodeBase64(dataString)))) {
            return comp.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
