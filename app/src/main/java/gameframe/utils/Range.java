package gameframe.utils;

import java.io.Serializable;

import singeltons.Randoms;

/**
 * The {@code Range} class defines a range between two float values (min and
 * max)
 * and provides utility methods for checking inclusion, interpolation, and
 * clamping.
 */
public class Range implements Serializable {
    public float min;
    public float max;

    /**
     * Constructs a {@code Range} object with specified min and max values.
     *
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     */
    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Checks if a value is inside the range, inclusive of min and max.
     *
     * @param value The value to check.
     * @return {@code true} if the value is inside the range, {@code false}
     *         otherwise.
     */
    public boolean inside(float value) {
        return value <= max && value >= min;
    }

    /**
     * Checks if a value is inside the range, exclusive of min and max.
     *
     * @param value The value to check.
     * @return {@code true} if the value is inside the range (excluding boundaries),
     *         {@code false} otherwise.
     */
    public boolean insideExlude(float value) {
        return value < max && value > min;
    }

    /**
     * Linearly interpolates within the range using a given factor.
     *
     * @param value A factor between 0 and 1 for interpolation.
     * @return The interpolated value within the range.
     */
    public float lerp(float value) {
        return min + (max - min) * value;
    }

    /**
     * Maps a value to a 0-1 range based on the current range.
     *
     * @param value The value to map.
     * @return The mapped value in the 0-1 range.
     */
    public float map(float value) {
        return (value - min) / (max - min);
    }

    /**
     * Clamps a value within the current range.
     *
     * @param value The value to clamp.
     * @return The clamped value.
     */
    public float clamp(float value) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Returns a random float value within the range.
     *
     * @return A random value between min and max.
     */
    public float random() {
        return Randoms.range(min, max);
    }
}
