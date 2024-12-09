package wtd.slotsengine.slots.machines.reels;

import java.util.ArrayList;
import java.util.Collections;

public class VirtualReelBuilder implements IReel {
    private final ArrayList<Integer> newList;

    public VirtualReelBuilder() {
        this.newList = new ArrayList<>(4096);
    }

    public static VirtualReelBuilder builder() {
        return new VirtualReelBuilder();
    }

    public VirtualReelBuilder addSymbol(int symbol, int count) {
        for (int i = 0; i < count; i++) {
            newList.add(symbol);
        }
        return this;
    }

    public int size() {
        return newList.size();
    }

    public int get(int index) {
        return newList.get(index % newList.size());
    }

    public VirtualReelBuilder sort() {
        Collections.sort(newList);
        return this;
    }

    public VirtualReelBuilder shuffle() {
        Collections.shuffle(newList);
        return this;
    }

    public VirtualReel build() {
        return new VirtualReel(newList);
    }
}
