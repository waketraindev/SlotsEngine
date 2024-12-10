package wtd.slotsengine.slots.machines.reels;

import wtd.slotsengine.utils.SlotConstants;
import wtd.slotsengine.utils.SlotUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualReel implements IReel {
    private final byte[] data;

    public VirtualReel(byte[] input) {
        data = input;
    }

    public VirtualReel(List<Byte> bytes) {
        data = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            data[i] = bytes.get(i);
        }
    }

    public int get(int position) {
        return (int) data[position % data.length];
    }

    public int size() {
        return data.length;
    }

    static public VirtualReel loadFromString(String dataString) {
        return new VirtualReel(parseElementsFromString(dataString));
    }

    static private List<Byte> parseElementsFromString(String dataString) {
        ArrayList<Byte> aList = new ArrayList<>();
        byte[] reelBytes = SlotUtils.decodeGzipBase64(dataString);
        for (byte reelByte : reelBytes) {
            aList.add(reelByte);
        }
        Collections.shuffle(aList, SlotConstants.RANDOM);
        return aList;
    }

    public String toString() {
        return SlotUtils.encodeGzipBase64(toByteArray());
    }

    public byte[] toByteArray() {
        int len = size();
        byte[] result = new byte[len];
        System.arraycopy(data, 0, result, 0, len);
        return result;
    }
}