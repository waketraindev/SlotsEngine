package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.abstracts.AbstractSlotMachine;

import java.util.ArrayList;
import java.util.List;

final public class BasicSlotMachine extends AbstractSlotMachine {
    private final List<Integer> reel = new ArrayList<>();

    public void addSymbol(int sym, int times) {
        for (int i = 0; i < times; i++) {
            reel.add(sym);
        }
    }

    @Override
    public long doSpin(long betAmount) {
        int position = getRandom().nextInt(reel.size());
        long winAmount = spinLogic(position, betAmount);
        if (winAmount > 0) {
            winCredits(winAmount);
        }
        return winAmount;
    }

    private long spinLogic(int position, long betAmount) {
        long winAmount;
        int res = reel.get(position);

        switch (res) {
            case 0 -> winAmount = 0;
            case 1 -> winAmount = betAmount;
            case 2 -> winAmount = betAmount * 2;
            case 3 -> winAmount = betAmount * 3;
            default -> throw new SlotUserException("Invalid symbol");
        }

        return winAmount;
    }

    public double calculateRTP() {
        long cost = 0L;
        long winAmount = 0L;
        for (int i = 0; i < reel.size(); i++) {
            winAmount += spinLogic(i, 1);
            cost += 1;
        }
        return winAmount / (double) cost;
    }
}
