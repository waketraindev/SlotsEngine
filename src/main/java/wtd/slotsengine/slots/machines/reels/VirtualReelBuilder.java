package wtd.slotsengine.slots.machines.reels;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A builder class for creating and customizing instances of {@code VirtualReel}.
 * This class provides methods to add symbols to the reel, shuffle or sort
 * the reel data, and ultimately build a {@code VirtualReel} instance.
 * <p>
 * The {@code VirtualReelBuilder} allows dynamic addition of symbols with
 * specified repetitions and ensures flexibility in constructing virtual
 * reels for applications like slot machine simulations.
 */
public class VirtualReelBuilder implements IReel {
    private final ArrayList<Byte> newList;

    /**
     * Constructs a new instance of {@code VirtualReelBuilder}.
     * Initializes an internal list with a default capacity of 4096 elements to store symbols
     * and their repetitions. This builder provides methods to configure and create a
     * {@code VirtualReel} instance.
     */
    public VirtualReelBuilder() {
        this.newList = new ArrayList<>(4096);
    }

    /**
     * Creates and returns a new instance of {@code VirtualReelBuilder}.
     * The builder provides methods to dynamically add symbols, sort or shuffle reel data,
     * and build a custom {@code VirtualReel} instance.
     *
     * @return a new {@code VirtualReelBuilder} instance for constructing {@code VirtualReel} objects.
     */
    public static VirtualReelBuilder builder() {
        return new VirtualReelBuilder();
    }

    /**
     * Adds a specified symbol to the reel a given number of times.
     * This allows the repetition of specific symbols in the final virtual reel configuration.
     *
     * @param symbol the symbol to be added to the reel, represented as a {@code Byte}.
     * @param count  the number of times the symbol should be added to the reel.
     * @return the current instance of {@code VirtualReelBuilder}, enabling method chaining.
     */
    public VirtualReelBuilder addSymbol(Byte symbol, int count) {
        for (int i = 0; i < count; i++) {
            newList.add(symbol);
        }
        return this;
    }

    /**
     * Retrieves the value at the specified index from the internal list.
     * If the index exceeds the size of the list, it wraps around using
     * modulo arithmetic to map the index within the valid range.
     *
     * @param index the position in the list whose value is to be retrieved.
     *              If the index exceeds the size of the list, it wraps
     *              around using the index modulo the list size.
     * @return the value located at the mapped index in the list.
     */
    public int get(int index) {
        return newList.get(index % newList.size());
    }

    /**
     * Returns the number of elements currently in the internal list.
     * This represents the total count of symbols or values added to the builder.
     *
     * @return the number of elements in the internal list.
     */
    public int size() {
        return newList.size();
    }

    /**
     * Sorts the internal list of elements in ascending order.
     * This method organizes the data to ensure it is arranged
     * in a sequentially sorted manner based on the natural ordering of the elements.
     *
     * @return the current instance of {@code VirtualReelBuilder}, allowing method chaining.
     */
    public VirtualReelBuilder sort() {
        Collections.sort(newList);
        return this;
    }

    /**
     * Randomly shuffles the order of elements in the internal list.
     * This method modifies the internal list by rearranging its elements
     * into a random sequence. It is useful for creating a randomized
     * configuration of symbols or data in the virtual reel.
     *
     * @return the current instance of {@code VirtualReelBuilder}, allowing method chaining.
     */
    public VirtualReelBuilder shuffle() {
        Collections.shuffle(newList);
        return this;
    }

    /**
     * Builds and returns a {@code VirtualReel} instance configured with the current state
     * of the builder. The {@code VirtualReel} is created using the internal data list
     * maintained in this builder.
     *
     * @return a new instance of {@code VirtualReel} constructed from the builder's data.
     */
    public VirtualReel build() {
        return new VirtualReel(newList);
    }
}