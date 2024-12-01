package wtd.slotsengine.utils;

import java.io.Serializable;
import java.util.Objects;

public class Credits implements Serializable {
    private final String name;
    private final int unitSize;
    private final long value;

    public Credits(String name, int minUnit, long value) {
        this.name = name;
        this.unitSize = minUnit;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUnitSize(), getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Credits credits)) return false;
        return Objects.equals(getName(), credits.getName()) && Objects.equals(getUnitSize(), credits.getUnitSize()) && Objects.equals(getValue(), credits.getValue());
    }

    @Override
    public String toString() {
        return Credits.displayString(name, unitSize, value);
    }

    public static String displayString(String name, int unitSize, long value) {
        return String.format("%.2f%s", (value / (double) unitSize), name);
    }

    public String getName() {
        return name;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public long getValue() {
        return value;
    }
}
