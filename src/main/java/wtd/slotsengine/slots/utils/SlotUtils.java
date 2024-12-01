package wtd.slotsengine.slots.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SlotUtils {
    public static String encodeGzipBase64(byte[] data) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            GZIPOutputStream comp = new GZIPOutputStream(output);
            comp.write(data, 0, data.length);
            comp.close();
            output.close();
            return Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decodeGzipBase64(String dataString) {
        try (GZIPInputStream comp = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(dataString)))) {
            return comp.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
