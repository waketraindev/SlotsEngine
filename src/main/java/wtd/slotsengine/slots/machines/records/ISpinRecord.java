package wtd.slotsengine.slots.machines.records;

/**
 * Represents a contract for the outcome of a single spin in a slot machine.
 * <p>
 * This interface defines the structure for accessing details of a spin, including:
 * - The amount wagered during the spin.
 * - The amount won as a result of the spin.
 * - The resulting symbol associated with the spin's outcome.
 * <p>
 * Implementations of this interface are expected to provide immutable, lightweight
 * data representations for interoperability across slot machine components.
 */
public interface ISpinRecord {
    /**
     * Retrieves the bet amount associated with a spin.
     *
     * @return the amount of credits wagered for the spin as a long value.
     */
    long betAmount();

    /**
     * Retrieves the amount of credits won as a result of a spin.
     *
     * @return the win amount in credits as a long value.
     */
    long winAmount();

    /**
     * Retrieves the resulting symbol associated with the slot machine spin's outcome.
     * The symbol is typically an integer value derived from the slot machine's virtual
     * reel, determining the payout multiplier or prize.
     *
     * @return the symbol representing the outcome of the spin as an integer.
     */
    int symbol();
}