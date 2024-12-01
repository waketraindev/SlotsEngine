package wtd.slotsengine.slots.machines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualReel {
    private final List<Integer> data = new ArrayList<>();

    public VirtualReel() {

    }

    public VirtualReel(String dataList) {
        for (int i = 0; i < dataList.length(); i++) {
            data.add(Integer.parseInt(dataList.substring(i, i + 1)));
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer e : data) {
            sb.append(e);
        }
        return sb.toString();
    }

    public int size() {
        return data.size();
    }

    public void shuffle() {
        Collections.shuffle(data);
    }
}
