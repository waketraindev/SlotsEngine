package wtd.slotsengine.slots.machines.reels;

import wtd.slotsengine.utils.SlotConstants;
import wtd.slotsengine.utils.SlotUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a virtual reel for use in slot machine simulations.
 * This class is an implementation of the {@code IReel} interface and provides methods for accessing and
 * manipulating reel data, which is represented as a sequence of bytes.
 */
public class VirtualReel implements IReel {
    private final byte[] data;

    /**
     * Constructs a {@code VirtualReel} instance using the provided byte array.
     * The byte array represents the sequence of symbols or data
     * for the virtual reel used in slot machine simulations.
     *
     * @param input the byte array representing the reel data. This array defines the
     *              sequence of symbols or values used by the virtual reel.
     */
    public VirtualReel(byte[] input) {
        data = input;
    }

    /**
     * Constructs a {@code VirtualReel} instance using the provided list of bytes.
     * Each element in the list represents a symbol or piece of data for the
     * virtual reel, which is utilized in slot machine simulations.
     *
     * @param bytes the list of {@code Byte} objects representing the reel data.
     *              Each byte corresponds to a symbol or value in the virtual reel.
     */
    public VirtualReel(List<Byte> bytes) {
        data = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            data[i] = bytes.get(i);
        }
    }

    /**
     * Creates a new {@code VirtualReel} instance by decoding and processing data from the provided string.
     * <p>
     * The method takes a Base64-encoded, GZIP-compressed string representing the serialized reel data,
     * decodes it into a byte array, and constructs a shuffled list of {@code Byte} objects. This shuffled
     * data is then used to create and initialize a {@code VirtualReel} instance.
     *
     * @param dataString the Base64-encoded, GZIP-compressed string containing the serialized reel data.
     *                   This string should represent the reel symbols or values in a compressed format.
     * @return a new {@code VirtualReel} instance initialized with the decoded and shuffled reel data.
     */
    static public VirtualReel loadFromString(String dataString) {
        return new VirtualReel(parseElementsFromString(dataString));
    }

    /**
     * Parses a Base64-encoded and GZIP-compressed string and converts it into
     * a shuffled list of {@code Byte} objects.
     *
     * @param dataString the Base64-encoded, GZIP-compressed string representing the serialized byte data
     * @return a {@code List} of {@code Byte} objects, representing the decoded and shuffled data
     */
    static private List<Byte> parseElementsFromString(String dataString) {
        ArrayList<Byte> aList = new ArrayList<>();
        byte[] reelBytes = SlotUtils.decodeGzipBase64(dataString);
        for (byte reelByte : reelBytes) {
            aList.add(reelByte);
        }
        Collections.shuffle(aList, SlotConstants.RANDOM);
        return aList;
    }

    /**
     * Retrieves the symbol or value from the reel located at the specified position.
     * The position is adjusted to wrap around the reel's length using modulo arithmetic,
     * ensuring that the position always maps to a valid index within the reel.
     *
     * @param position the position in the reel from which to retrieve the value. If the position
     *                 is greater than the reel size, it will wrap around using modulo operation.
     * @return the symbol or value at the specified position in the reel.
     */
    public int get(int position) {
        return data[position % data.length];
    }

    /**
     * Returns the size of the reel, which corresponds to the number of symbols or
     * elements stored in the reel data.
     *
     * @return the number of elements in the reel data.
     */
    public int size() {
        return data.length;
    }

    /**
     * Converts the current {@code VirtualReel} instance to its string representation.
     * The representation is a Base64 encoded, GZIP compressed string of the reel's
     * byte array data.
     *
     * @return a string representing the compressed and encoded data of the reel.
     */
    public String toString() {
        return SlotUtils.encodeGzipBase64(toByteArray());
    }

    /**
     * Converts the current state of the VirtualReel into a byte array.
     * The generated array contains all the elements of the reel's data
     * in the same order as they were stored.
     *
     * @return a byte array representation of the VirtualReel's data.
     */
    public byte[] toByteArray() {
        int len = size();
        byte[] result = new byte[len];
        System.arraycopy(data, 0, result, 0, len);
        return result;
    }
}