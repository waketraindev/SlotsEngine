package wtd.slotsengine.slots.machines;

import wtd.slotsengine.slots.exceptions.SlotUserException;
import wtd.slotsengine.slots.machines.abstracts.AbstractSlotMachine;

import java.util.ArrayList;
import java.util.List;

final public class BasicSlotMachine extends AbstractSlotMachine {
    private final VirtualReel reel;

    public BasicSlotMachine() {
        super();
        //47, 19, 17, 13
        reel = new VirtualReel("030122000002000000000023200003020103012021102102112200031312210103020013133010000122000010133013");
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
            default -> throw new SlotUserException("Invalid symbol " + res);
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
