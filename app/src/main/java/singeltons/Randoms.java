package singeltons;

import java.util.Random;

/**
 * Utility class that provides methods for generating random floating-point
 * numbers.
 */
public class Randoms {
    private static Random random;

    /**
     * Generates a random floating-point number between a specified range [min,
     * max).
     * If the {@code min} value is greater than the {@code max} value, the method
     * swaps them.
     *
     * @param min The lower bound (inclusive) of the range.
     * @param max The upper bound (exclusive) of the range.
     * @return A random float value between {@code min} (inclusive) and {@code max}
     * (exclusive).
     */
    public static float range(float min, float max) {
        // Initialize the random object if it hasn't been already
        if (random == null)
            random = new Random();

        // Swap min and max if min is greater than max
        if (min > max) {
            float temp = min;
            min = max;
            max = temp;
        }

        // Generate and return the random float
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Generates a random floating-point number between 0.0 (inclusive) and 1.0
     * (exclusive).
     *
     * @return A random float value between 0.0 (inclusive) and 1.0 (exclusive).
     */
    public static float range01() {
        // Initialize the random object if it hasn't been already
        if (random == null)
            random = new Random();

        // Return a random float between 0.0 and 1.0
        return random.nextFloat();
    }

    public static int randomInt() {
        if (random == null)
            random = new Random();

        return random.nextInt();
    }

    /**
     * returns a random int between 0 and MAX_VALUE - 1
     * @return
     */
    public static int randomPosInt() {
        if (random == null)
            random = new Random();

        return random.nextInt(Integer.MAX_VALUE);
    }

    /**
     * Generates a random int number between a specified range [min,
     * max).
     * If the {@code min} value is greater than the {@code max} value, the method
     * swaps them.
     *
     * @param min The lower bound (inclusive) of the range.
     * @param max The upper bound (exclusive) of the range.
     * @return A random float value between {@code min} (inclusive) and {@code max}
     * (exclusive).
     */
    public static int rangeInt(int min, int max) {
        if (random == null)
            random = new Random();

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return min + random.nextInt(max - min);
    }
}
