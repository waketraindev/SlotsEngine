package wtd.slotsengine.slots.machines.reels;

import wtd.slotsengine.utils.SlotConstants;
import wtd.slotsengine.utils.SlotUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualReel implements IReel {
    private final List<Integer> data;

    public VirtualReel(List<Integer> symbolList) {
        data = Collections.unmodifiableList(symbolList);
    }

    public int get(int position) {
        return (int) data.get(position % data.size());
    }

    public int size() {
        return data.size();
    }

    static public VirtualReel loadFromString(String dataString) {
        return new VirtualReel(parseElementsFromString(dataString));
    }

    static private List<Integer> parseElementsFromString(String dataString) {
        ArrayList<Integer> aList = new ArrayList<>();
        byte[] reelBytes = SlotUtils.decodeGzipBase64(dataString);
        for (byte reelByte : reelBytes) {
            aList.add((int)reelByte);
        }
        Collections.shuffle(aList, SlotConstants.RANDOM);
        return aList;
    }

    public String toString() {
        return SlotUtils.encodeGzipBase64(toByteArray());
    }

    public byte[] toByteArray() {
        int len = data.size();
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = data.get(i).byteValue();
        }
        return result;
    }
}
