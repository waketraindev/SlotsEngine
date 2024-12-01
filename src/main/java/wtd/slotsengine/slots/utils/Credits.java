package wtd.slotsengine.slots.utils;

import java.io.Serializable;
import java.util.Objects;

public class Credits implements Serializable, Comparable<Credits> {
    private final String name;
    private final Integer unitSize;
    private final Long value;

    public Credits(String name, Integer minUnit, Long value) {
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

    public static String displayString(String name, Integer unitSize, Long value) {
        return String.format("%.2f%s", (value / unitSize.doubleValue()), name);

    }

    public String getName() {
        return name;
    }

    public Integer getUnitSize() {
        return unitSize;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public int compareTo(Credits o) {
        return this.getValue().compareTo(o.getValue());
    }
}
