package wtd.slotsengine.slots;

import wtd.slotsengine.slots.machines.BasicSlotMachine;
import wtd.slotsengine.slots.machines.VirtualReel;
import wtd.slotsengine.slots.utils.Credits;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.LongSummaryStatistics;

public class ConsoleSlotsApp {
    public static void main(String[] args) {
        BasicSlotMachine sm = new BasicSlotMachine();
        System.out.println("RTP: " + sm.calculateRTP());

        long totalBet = 0L;
        long totalWon = 0L;
        LongSummaryStatistics stats = new LongSummaryStatistics();
        long[] betRanges = new long[]{1L, 5L, 10L, 25L, 50L, 100L, 250L, 500L, 1000L};
        while ((totalBet - totalWon) < 100_000_000) {
            sm.deposit(1000);
            for (int i = 0; i < 50 && sm.hasCredits(); i++) {
                long balance = sm.getBalance();
                int betPosition = sm.getRandom().nextInt(betRanges.length);
                long betAmount = betRanges[betPosition];
                while (betAmount > balance) {
                    betPosition = Math.max(0, betPosition - 1);
                    betAmount = betRanges[betPosition];
                }
                long winAmount = sm.spin(betAmount);
                totalWon += winAmount;
                totalBet += betAmount;
                stats.accept(balance);
            }
            sm.withdraw(sm.getBalance());
        }
        System.out.println("Run Over: " + (totalBet - totalWon));
        System.out.println(stats);

    }
}
