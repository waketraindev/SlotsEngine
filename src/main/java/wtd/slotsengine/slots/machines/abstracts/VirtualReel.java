package wtd.slotsengine.slots.machines.abstracts;

import wtd.slotsengine.utils.SlotUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualReel {
    private final List<Byte> data;

    public VirtualReel(String dataString) {
        data = VirtualReel.parseElementsFromString(dataString, true);
    }

    public Integer get(int position) {
        return (int) data.get(position % data.size());
    }

    public int size() {
        return data.size();
    }

    static public VirtualReel loadFromString(String dataString) {
        return new VirtualReel(dataString);
    }

    static private List<Byte> parseElementsFromString(String dataString, boolean doShuffle) {
        ArrayList<Byte> aList = new ArrayList<>();
        byte[] reelBytes = SlotUtils.decodeGzipBase64(dataString);
        for (byte reelByte : reelBytes) {
            aList.add(reelByte);
        }
        if (doShuffle) Collections.shuffle(aList);
        return Collections.unmodifiableList(aList);
    }

    public String toString() {
        return SlotUtils.encodeGzipBase64(toByteArray());
    }

    public byte[] toByteArray() {
        int len = data.size();
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = data.get(i);
        }
        return result;
    }
}
