package wtd.slotsengine.slots.machines.records;

/**
 * Represents the outcome of a single spin in a slot machine.
 * <p>
 * This record encapsulates the details of a spin, including the bet amount,
 * the win amount, the symbol that resulted from the spin, and the player's
 * remaining balance after the spin.
 * <p>
 * The {@code betAmount} indicates the amount wagered during the spin. The
 * {@code winAmount} reflects the payout received based on the spin's result.
 * The {@code symbol} represents the outcome of the slot machine's reel spin,
 * which is often used to determine win conditions. The {@code balance} shows
 * the user's credit balance after the spin has been processed.
 * <p>
 * This record is immutable and is used for conveying spin results across
 * slot machine components, ensuring consistency and thread safety.
 */
public record SpinOutcome(long betAmount, long winAmount, int symbol, long balance) implements ISpinRecord {
}