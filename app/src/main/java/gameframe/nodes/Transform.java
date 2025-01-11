package gameframe.nodes;

import gameframe.utils.Vec2;

/**
 * Represents position data of the attached node.
 */
public class Transform {
    /**
     * Worldspace position of the node always relative to the origin (0,0).
     */
    private Vec2 pos = new Vec2(0, 0);
    /**
     * 2d scale of the node.
     */
    private Vec2 scale = new Vec2(1, 1);

    public Vec2 pos() {
        return pos.clone();
    }

    public void setPos(Vec2 pos) {
        if (pos != null)
            this.pos = pos;
    }

    public Vec2 scale() {
        return scale.clone();
    }

    public void setScale(Vec2 scale) {
        if (scale != null)
            this.scale = scale;
    }

    public void copyValues(Transform other) {
        if (other == null)
            return;
        setPos(other.pos());
        setScale(other.scale());
        roation = other.roation;
    }

    /**
     * Rotation of the node (radians). Only effects visual aspects.
     */
    public float roation;
}
