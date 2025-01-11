package gameframe.utils.quadtrees;

import java.util.LinkedList;
import java.util.List;

import gameframe.conductors.Conductor;
import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.collision.ICollider;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class OverlapRect implements ICollider {
    private ColliderRect rect;

    public void set(Vec2 pos, Vec2 size) {
        rect = new ColliderRect(pos.add(size.mult(new Vec2(-0.5f, 0.5f))), size, this);
    }

    public OverlapRect(Vec2 pos, Vec2 size) {
        set(pos, size);
    }

    public OverlapRect(Vec2 pos, Vec2 size, Conductor conductor) {
        set(pos, size);
        callCollisionCheck(conductor);
    }


    public OverlapRect(Vec2 pos, Vec2 size, int theyAre, Conductor conductor) {
        set(pos, size);
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
        return false;
    }

    @Override
    public Vec2 currentCenterPos() {
        return rect.pos.add(rect.size.mult(new Vec2(0.5f, -0.5f)));
    }

    @Override
    public ColliderRect rect() {
        return rect;
    }
}
