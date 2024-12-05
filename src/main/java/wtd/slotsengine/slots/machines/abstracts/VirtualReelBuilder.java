package wtd.slotsengine.slots.machines.abstracts;

import java.util.ArrayList;
import java.util.Collections;

public class VirtualReelBuilder {
    private final ArrayList<Byte> newList;

    public VirtualReelBuilder() {
        this.newList = new ArrayList<>();
    }

    public static VirtualReelBuilder builder() {
        return new VirtualReelBuilder();
    }

    public VirtualReelBuilder addSymbol(int symbol, int count) {
        for (int i = 0; i < count; i++) {
            newList.add((byte) symbol);
        }
        return this;
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
