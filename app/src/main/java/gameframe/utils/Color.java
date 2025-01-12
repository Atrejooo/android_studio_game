package gameframe.utils;

import java.io.Serializable;

import singeltons.Randoms;

public class Color implements Serializable {
    private float r, g, b, a;

    // Constructor with all parameters (with clamping)
    public Color(float r, float g, float b, float a) {
        this.r = AddF.clamp(r, 0, 1);
        this.g = AddF.clamp(g, 0, 1);
        this.b = AddF.clamp(b, 0, 1);
        this.a = AddF.clamp(a, 0, 1);
    }

    // Constructor with three parameters, defaulting alpha to 1 (opaque)
    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    // Default constructor (initializes color to black with full opacity)
    public Color() {
        this(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public Color(float value) {
        this(value, value, value, value);
    }

    // Getters
    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    public float a() {
        return a;
    }

    // Setters with clamping
    public void setR(float r) {
        this.r = AddF.clamp(r, 0, 1);
    }

    public void setG(float g) {
        this.g = AddF.clamp(g, 0, 1);
    }

    public void setB(float b) {
        this.b = AddF.clamp(b, 0, 1);
    }

    public void setA(float a) {
        this.a = AddF.clamp(a, 0, 1);
    }

    // Generates a random max saturation color
    public static Color randomMaxSaturationColor() {
        // Randomly pick one channel to be 1, one channel to be 0, and the last to be in range (0, 1)
        int randomChannel = (int)(Randoms.range01() * 6); // Generates a value in range [0, 5]
        float midValue = Randoms.range01(); // Generates a value in range [0, 1]

        float r = 0, g = 0, b = 0;

        switch (randomChannel) {
            case 0: r = 1; g = 0; b = midValue; break; // Red max, green min
            case 1: r = 1; g = midValue; b = 0; break; // Red max, blue min
            case 2: r = 0; g = 1; b = midValue; break; // Green max, red min
            case 3: r = midValue; g = 1; b = 0; break; // Green max, blue min
            case 4: r = 0; g = midValue; b = 1; break; // Blue max, red min
            case 5: r = midValue; g = 0; b = 1; break; // Blue max, green min
        }

        return new Color(r, g, b, 1.0f); // Full opacity by default
    }
}
