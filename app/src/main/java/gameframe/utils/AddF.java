package gameframe.utils;

public class AddF {
    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static final float PI = (float) Math.PI;
}
