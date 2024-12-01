package wtd.slotsengine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SlotUtils {
    public static final String PROJECT_VERSION = "0.0.1";

    public static Long now() {
        return System.currentTimeMillis();
    }

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

    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static byte[] decodeGzipBase64(String dataString) {
        try (GZIPInputStream comp = new GZIPInputStream(new ByteArrayInputStream(decodeBase64(dataString)))) {
            return comp.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
