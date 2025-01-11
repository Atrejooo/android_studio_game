package gameframe.utils.quadtrees;

import gameframe.utils.Vec2;

public class Rect {
    public Vec2 pos = new Vec2();
    public Vec2 size = new Vec2();

    public Rect(Vec2 pos, Vec2 size) {
        this.pos = pos;
        this.size = size;
    }

    /**
     * Returns if this {@code Rect} is overlapping with {@code other}.
     * @param other the {@code Rect} to check with
     * @return a boolean value -> true if overlapping, false if not overlapping
     */
    public boolean overlaps(Rect other) {
        return !(this.pos.x + this.size.x <= other.pos.x ||
                other.pos.x + other.size.x <= this.pos.x ||
                this.pos.y - this.size.y >= other.pos.y ||
                other.pos.y - other.size.y >= this.pos.y);
    }

    /**
     * Returns if this {@code Rect} is containing {@code other}.
     * @param other the {@code Rect} to check with
     * @return a boolean value -> true if this is containing, false if this is not containing {@code other}
     */
    public boolean contains(Rect other) {
        return this.pos.x <= other.pos.x && this.pos.x + this.size.x >= other.pos.x + other.size.x &&
                this.pos.y >= other.pos.y && this.pos.y - this.size.y <= other.pos.y - other.size.y;
    }

    /**
     * Checks if a point is inside this rectangle.
     * @param point the {@code Vec2} point to check
     * @return true if the point is inside the rectangle, false otherwise
     */
    public boolean contains(Vec2 point) {
        return point.x >= this.pos.x &&
                point.x <= this.pos.x + this.size.x &&
                point.y <= this.pos.y &&
                point.y >= this.pos.y - this.size.y;
    }
    /**
     * Checks if a circle overlaps with this rectangle.
     * @param centerPos the center position of the circle
     * @param r the radius of the circle
     * @return true if the circle overlaps with this rectangle, false otherwise
     */
    public boolean overlapsCircle(Vec2 centerPos, float r) {
        // Find the closest point on the rectangle to the circle's center
        float closestX = Math.max(this.pos.x, Math.min(centerPos.x, this.pos.x + this.size.x));
        float closestY = Math.max(this.pos.y - this.size.y, Math.min(centerPos.y, this.pos.y));

        // Calculate the distance between the circle's center and this closest point
        float distanceX = centerPos.x - closestX;
        float distanceY = centerPos.y - closestY;

        // If the distance is less than the circle's radius, there is an overlap
        return (distanceX * distanceX + distanceY * distanceY) <= (r * r);
    }


    public Rect[] quarters() {
        // Calculate half width and half height for the new rectangles
        float halfWidth = this.size.x / 2;
        float halfHeight = this.size.y / 2;

        // Calculate positions of the four new rectangles
        Vec2 topLeftPos = new Vec2(this.pos.x, this.pos.y);
        Vec2 topRightPos = new Vec2(this.pos.x + halfWidth, this.pos.y);
        Vec2 bottomLeftPos = new Vec2(this.pos.x, this.pos.y - halfHeight);
        Vec2 bottomRightPos = new Vec2(this.pos.x + halfWidth, this.pos.y - halfHeight);

        // Create the new rectangles
        Rect topLeft = new Rect(topLeftPos, new Vec2(halfWidth, halfHeight));
        Rect topRight = new Rect(topRightPos, new Vec2(halfWidth, halfHeight));
        Rect bottomLeft = new Rect(bottomLeftPos, new Vec2(halfWidth, halfHeight));
        Rect bottomRight = new Rect(bottomRightPos, new Vec2(halfWidth, halfHeight));

        // Return the array of new rectangles
        return new Rect[] { topLeft, topRight, bottomLeft, bottomRight };
    }

    @Override
    public String toString(){
        return "point: " + pos + ", size: " + size;
    }
}
