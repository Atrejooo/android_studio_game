package gameframe.functionalities.collision;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gameframe.conductors.Conductor;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;
import gameframe.utils.quadtrees.ColliderRect;
import gameframe.utils.quadtrees.Rect;

public class OverlapCircle implements ICollider {
    private Vec2 pos;
    private float radius;

    public Vec2 pos() {
        return pos.clone();
    }

    public float radius() {
        return radius;
    }

    public void set(float radius, Vec2 pos) {
        if (pos != null)
            this.pos = pos;
        this.radius = Math.abs(radius);
    }

    public OverlapCircle(float radius, Vec2 pos) {
        set(radius, pos);
    }

    public OverlapCircle(float radius, Vec2 pos, Conductor conductor) {
        set(radius, pos);
        callCollisionCheck(conductor);
    }

    public OverlapCircle(float radius, Vec2 pos, int theyAre, Conductor conductor) {
        set(radius, pos);
        this.theyAre = theyAre;
        callCollisionCheck(conductor);
    }

    private void callCollisionCheck(Conductor conductor) {
        conductor.collisionManager().overlapShape(this);
    }

    @Override
    public int thisIs() {
        return 0;
    }

    private int theyAre = Integer.MAX_VALUE;


    @Override
    public int theyAre() {
        return theyAre;
    }

    @Override
    public void setThisIs(int value) {

    }

    @Override
    public void setTheyAre(int value) {
        theyAre = value;
    }

    @Override
    public void addCollision(ICollider partner) {
        if (partner == null)
            return;

        if (!(partner instanceof Collider)) {
            return;
        }

        collisions.add(((Collider) partner).node());
    }

    @Override
    public void clearCollisions() {
        collisions.clear();
    }

    private LinkedList<Node> collisions = new LinkedList<>();


    @Override
    public List<Node> collisions() {
        return collisions;
    }

    @Override
    public boolean isCircle() {
        return true;
    }

    @Override
    public Vec2 currentCenterPos() {
        return pos;
    }

    @Override
    public ColliderRect rect() {
        return new ColliderRect(pos.add(new Vec2(-radius, radius)), new Vec2(radius * 2f), this);
    }
}
