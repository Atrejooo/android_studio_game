package gameframe.functionalities.collision;

import java.util.LinkedList;
import java.util.List;

import gameframe.conductors.Conductor;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.nodes.*;
import gameframe.utils.Color;
import gameframe.utils.Vec2;
import gameframe.utils.quadtrees.ColliderRect;

public class Collider extends Comp implements ICollider {
    private static final boolean display = false;

    public Collider(Node node) {
        super(node);
    }

    //layer
    private int thisIs = 0b0000000000000001;
    private int theyAre = 0b0000000000000001;

    @Override
    public int thisIs() {
        return thisIs;
    }

    @Override
    public int theyAre() {
        return theyAre;
    }

    @Override
    public void setThisIs(int value) {
        thisIs = value;
    }

    @Override
    public void setTheyAre(int value) {
        theyAre = value;
    }
    //layer end :(

    //collision list
    private LinkedList<Node> collisions = new LinkedList<>();

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

    @Override
    public List<Node> collisions() {
        return collisions;
    }


    //is this collider a circle
    private boolean isCircle = false;

    @Override
    public boolean isCircle() {
        return isCircle;
    }

    //the size of the collider
    private Vec2 size = Vec2.one();

    public Vec2 size() {
        return size;
    }

    public Vec2 scaledSize() {
        if (isCircle)
            return size.mult(transform().scale().abs().max());
        else
            return size.mult(transform().scale().abs());
    }

    //set this up as a rect collider
    public void setRect(Vec2 size) {
        this.size = size.clone();
        isCircle = false;
    }

    //set this up as a circle collider
    public void setCircle(float radius) {
        size = new Vec2(radius);
        isCircle = true;
    }

    //offset of the collider center
    private Vec2 centerOffset = new Vec2();

    public Vec2 centerOffset() {
        return centerOffset;
    }

    public Vec2 scaledCenterOffset() {
        return centerOffset.mult(transform().scale());
    }

    public void setCenterOffset(Vec2 centerOffset) {
        if (centerOffset != null)
            this.centerOffset = centerOffset;
    }

    @Override
    public Vec2 currentCenterPos() {
        return transform().pos().add(scaledCenterOffset());
    }

    //returns a rectangle representing the bounds of the collider
    @Override
    public ColliderRect rect() {

        Vec2 rectSize;

        if (isCircle)
            rectSize = size.mult(transform().scale().abs().max());
        else
            rectSize = size.mult(transform().scale().abs());

        Vec2 rectPos = centerOffset.mult(transform().scale()).add(new Vec2(-rectSize.x * 0.5f, rectSize.y * 0.5f));
        ColliderRect rect = new ColliderRect(transform().pos().add(rectPos), rectSize, this);

        return rect;
    }


    @Override
    public void setConductor(Conductor conductor) {
        super.setConductor(conductor);
        conductor.collisionManager().add(this);
    }

    @Override
    public void removeConductor() {
        if (conductor != null)
            conductor.collisionManager().remove(this);
        super.removeConductor();
    }


    @Override
    public void update() {
        if (!display)
            return;

        ImgRenderer imgRenderer = node().get(ImgRenderer.class);

        if (imgRenderer == null)
            return;

        if (collisions.size() > 0) {
            imgRenderer.data().setColor(new Color(0, 1, 0, 1));
        } else {
            imgRenderer.data().setColor(new Color(1, 0, 0, 1));
        }
    }
}
