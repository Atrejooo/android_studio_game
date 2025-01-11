package gameframe.utils;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A 2D vector class that provides basic vector operations such as addition,
 * subtraction,
 * multiplication, and division, along with geometric properties like magnitude,
 * normalization,
 * and angle calculations.
 */
public class Vec2 implements Serializable {

    /**
     * The x component of the vector.
     */
    public float x;

    /**
     * The y component of the vector.
     */
    public float y;

    /**
     * Constructs a new Vec2 object with specified x and y values.
     *
     * @param x The x component of the vector.
     * @param y The y component of the vector.
     */
    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * new v(0, 0)
     */
    public Vec2() {
    }

    public Vec2(float xy) {
        this.x = xy;
        this.y = xy;
    }

    public static Vec2 one() {
        return new Vec2(1, 1);
    }

    public Vec2 abs() {
        return new Vec2(Math.abs(x), Math.abs(y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Vec2) {
            Vec2 other = (Vec2) o;
            return other.x == x && other.y == y;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(x) ^ Float.floatToIntBits(y);
    }

    /**
     * Adds another vector to this vector.
     *
     * @param addVec The vector to be added.
     * @return A new vector which is the sum of this vector and addVec.
     */
    public Vec2 add(Vec2 addVec) {
        Vec2 newVec = new Vec2(x + addVec.x, y + addVec.y);
        return newVec;
    }

    /**
     * Subtracts another vector from this vector.
     *
     * @param subVec The vector to be subtracted.
     * @return A new vector which is the difference between this vector and subVec.
     */
    public Vec2 sub(Vec2 subVec) {
        Vec2 newVec = new Vec2(x - subVec.x, y - subVec.y);
        return newVec;
    }

    /**
     * Multiplies this vector by a scalar factor.
     *
     * @param factor The scalar factor.
     * @return A new vector which is this vector multiplied by the scalar factor.
     */
    public Vec2 mult(float factor) {
        Vec2 newVec = new Vec2(x * factor, y * factor);
        return newVec;
    }

    /**
     * Multiples this with factor (x * x and y * y)
     *
     * @param factor The vector that this is to be multiplied by
     * @return The multipled vector
     */
    public Vec2 mult(Vec2 factor) {
        return new Vec2(x * factor.x, y * factor.y);
    }


    /**
     * Divides this vector by a scalar divisor.
     *
     * @param divisor The scalar divisor.
     * @return A new vector which is this vector divided by the scalar divisor.
     */
    public Vec2 div(float divisor) {
        Vec2 newVec = new Vec2(x / divisor, y / divisor);
        return newVec;
    }

    /**
     * Divides this vector by another vector (x / x, y / y) divisor.
     *
     * @param divisor The vector divisor.
     * @return A new vector which is this vector divided by the vector divisor.
     */
    public Vec2 div(Vec2 divisor) {
        Vec2 newVec = new Vec2(x / divisor.x, y / divisor.y);
        return newVec;
    }

    /**
     * Retruns the bigger component (x or y)
     */
    public float max() {
        if (x > y)
            return x;
        return y;
    }

    /**
     * Calculates the magnitude (length) of the vector.
     *
     * @return The magnitude of the vector.
     */
    public float mag() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vec2 clampMag(float min, float max) {
        float mag = mag();
        mag = AddF.clamp(mag, min, max);

        return normal().mult(mag);
    }

    /**
     * Calculates the squared magnitude of the vector (faster than mag()).
     *
     * @return The squared magnitude of the vector.
     */
    public float sqMag() {
        return x * x + y * y;
    }

    /**
     * Normalizes the vector, returning a unit vector.
     *
     * @return A new normalized vector, or (0, 0) if the vector's magnitude is 0.
     */
    public Vec2 normal() {
        float mag = mag();
        if (mag != 0)
            return div(mag);
        return new Vec2(0, 0);
    }

    /**
     * Normalizes the vector, returning a unit vector.
     *
     * @return A new normalized vector, or (0, 0) if the vector's magnitude is 0.
     */
    public Vec2 normal(float mag) {
        if (mag > 0)
            return div(mag);
        return new Vec2(0, 0);
    }


    /**
     * Calculates the distance between this vector and another vector.
     *
     * @param point The other vector.
     * @return The distance between the two vectors.
     */
    public float distance(Vec2 point) {
        return this.sub(point).mag();
    }

    /**
     * Returns a string representation of the vector.
     *
     * @return A string in the format "vec(x, y)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Flips this vector around another vector t.
     *
     * @param t The vector around which to flip.
     * @return A new vector representing the flipped vector.
     */
    public Vec2 flip(Vec2 t) {
        float vx = ((t.x * t.x - t.y * t.y) * x + 2 * t.x * t.y * y) / (t.x * t.x + t.y * t.y);
        float vy = (2 * t.x * t.y * x - (t.x * t.x - t.y * t.y) * y) / (t.x * t.x + t.y * t.y);

        return new Vec2(vx, vy);
    }

    /**
     * Computes the dot product of this vector with another vector.
     *
     * @param v The other vector.
     * @return The dot product of the two vectors.
     */
    public float dot(Vec2 v) {
        return x * v.x + y * v.y;
    }

    /**
     * Rotates this vector by 90 degrees.
     *
     * @return A new vector that is rotated 90 degrees.
     */
    public Vec2 rot90() {
        return new Vec2(-x, y);
    }

    /**
     * Calculates the angle between this vector and another vector.
     *
     * @param v The other vector.
     * @return The angle in radians between the two vectors.
     */
    public float angleWithOther(Vec2 v) {
        float mag1 = mag();
        float mag2 = v.mag();
        if (mag1 + mag2 == 0)
            return 0;

        return (float) Math.acos(Math.abs(dot(v)) / (mag1 * mag2));
    }

    /**
     * Rotates this vector around the origin (0,0) by a given angle in radians.
     *
     * @param angle The angle to rotate the vector, in radians.
     * @return A new vector that is the result of rotating this vector by the specified angle.
     */
    public Vec2 rotate(float angle) {
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        float newX = x * cosTheta - y * sinTheta;
        float newY = x * sinTheta + y * cosTheta;

        return new Vec2(newX, newY);
    }

    /**
     * Calculates the angle of the vector in radians relative to the positive
     * x-axis.
     *
     * @return The angle in radians.
     */
    public float atan() {
        if (x == 0)
            return (float) Math.PI / 2f;
        return (float) Math.atan2(y, x);
    }

    /**
     * Creates a clone of this vector.
     *
     * @return A new vector that is a copy of this vector.
     */
    public Vec2 clone() {
        return new Vec2(x, y);
    }

    /**
     * Compares this vector with another vector.
     *
     * @param other The other vector.
     * @return true if the vectors are equal, false otherwise.
     */
    public boolean compare(Vec2 other) {
        return x == other.x && y == other.y;
    }

    /**
     * Determines the sector (quadrant) of the vector in the Cartesian plane.
     *
     * @return An integer representing the sector:
     * 0 for the first or fourth quadrants (x > 0),
     * 1 for the second quadrant (x < 0, y > 0),
     * 2 for the third quadrant (x < 0, y < 0),
     * 3 for the fourth quadrant (x > 0, y < 0).
     */
    public int getqQuadrant() {
        if (x > 0) {
            if (y > 0) {
                return 0;
            } else if (y < 0) {
                return 3;
            }
            return 0;
        } else if (x < 0) {
            if (y > 0) {
                return 1;
            } else if (y < 0) {
                return 2;
            }
            return 2;
        } else {
            if (y > 0) {
                return 1;
            } else if (y < 0) {
                return 3;
            }
            return 0;
        }
    }

    /**
     * Creates a vector from a given angle in radians.
     *
     * @param angle The angle in radians.
     * @return A new vector with components based on the given angle.
     */
    public static Vec2 fromAngle(float angle) {
        return new Vec2((float) Math.cos(angle), (float) Math.sin(angle));
    }
}
