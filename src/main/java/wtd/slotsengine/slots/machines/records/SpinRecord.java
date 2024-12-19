package wtd.slotsengine.slots.machines.records;

/**
 * Represents the outcome of a single spin in a slot machine.
 * <p>
 * This record encapsulates the bet amount, the win amount, and the resulting symbol
 * associated with a slot machine spin. It serves as a lightweight, immutable data
 * structure that implements the {@link ISpinRecord} interface for compatibility
 * across different slot machine components.
 * <p>
 * The {@code betAmount} represents the credits wagered by the user, while the
 * {@code winAmount} represents the amount won during the spin based on the resulting
 * {@code symbol}. The symbol is typically an integer value derived from the slot machine's
 * virtual reel, which determines the payout multiplier or prize.
 */
public record SpinRecord(long betAmount, long winAmount, int symbol) implements ISpinRecord {
}