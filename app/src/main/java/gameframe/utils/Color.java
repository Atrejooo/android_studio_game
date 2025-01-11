package gameframe.utils;

public class Color {
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

    public Color(float t, float a) {
        // Normalize t to a range of 0 to 1 (wrap around if t is outside the range)
        t = t - (float) Math.floor(t); // Ensures 0 <= t < 1

        // Determine the segment (0.0–1.0 is split into 3 segments)
        float segment = t * 3; // Scale to range 0–3
        int iSegment = (int) segment; // Segment index (0 to 2)
        float localT = segment - iSegment; // Local t within the segment (0 to 1)

        // Set RGB based on the segment
        switch (iSegment) {
            case 0: // Red to Green (R:1, G:0 → G:1, R:0)
                this.r = 1.0f - localT;
                this.g = localT;
                this.b = 0.0f;
                break;
            case 1: // Green to Blue (G:1, B:0 → B:1, G:0)
                this.r = 0.0f;
                this.g = 1.0f - localT;
                this.b = localT;
                break;
            case 2: // Blue to Red (B:1, R:0 → R:1, B:0)
                this.r = localT;
                this.g = 0.0f;
                this.b = 1.0f - localT;
                break;
            default:
                // Should never reach here
                this.r = 1.0f;
                this.g = 0.0f;
                this.b = 0.0f;
        }

        // Default alpha to 1.0 (fully opaque)
        this.a = a;
    }
}
