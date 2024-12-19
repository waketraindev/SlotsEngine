package wtd.slotsengine.slots.machines.reels;

/**
 * Represents a reel interface for implementing reel-based operations, commonly
 * used in slot machine simulations or other applications involving arrays of
 * symbols or data elements.
 * <p>
 * The {@code IReel} interface defines the contract for retrieving elements
 * from a given position in the reel and obtaining the overall size of the
 * reel. Implementing classes provide the underlying data structure and
 * logic for these operations.
 */
public interface IReel {
    /**
     * Retrieves the value or symbol present at the specified position in the reel.
     * Positions exceeding the reel size are adjusted by applying modulo arithmetic,
     * ensuring the position maps correctly within the reel's data range.
     *
     * @param position the position in the reel whose value is to be retrieved.
     *                 If the position exceeds the reel size, it wraps around
     *                 using the position modulo the reel size.
     * @return the value or symbol located at the specified position in the reel.
     */
    int get(int position);

    /**
     * Returns the size of the reel, indicating the number of symbols
     * or elements stored in the reel's data structure.
     *
     * @return the total number of elements in the reel.
     */
    int size();
}