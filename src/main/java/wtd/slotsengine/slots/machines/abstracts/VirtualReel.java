package wtd.slotsengine.slots.machines.abstracts;

import wtd.slotsengine.utils.SlotUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualReel {
    private final List<Integer> data = new ArrayList<>();

    public VirtualReel() {
    }

    public VirtualReel(String dataString) {
        addElementsFromString(dataString);
    }

    public void addElementsFromString(String dataString) {
        byte[] reelBytes = SlotUtils.decodeGzipBase64(dataString);
        for (byte reelByte : reelBytes) {
            data.add((int) reelByte);
        }
    }

    public void addSymbol(int sym, int times) {
        for (int i = 0; i < times; i++) {
            data.add(sym);
        }
    }

    public Integer get(int position) {
        return data.get(position % data.size());
    }

    public String encodeToString() {
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

    public int size() {
        return data.size();
    }

    public void shuffle() {
        Collections.shuffle(data);
    }

    public void sort() {
        Collections.sort(data);
    }
}
