package gameframe.functionalities.movement;

import android.util.Log;

import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.collision.CollisionObserver;
import gameframe.functionalities.collision.OverlapCircle;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class VelocityBounceMovement extends VelocityMovement {
    public VelocityBounceMovement(Node node) {
        super(node);
    }

    private Collider collider;
    private float bounceF = 1f;

    public void setBounceF(float bounceF) {
        this.bounceF = bounceF;
    }

    private CollisionObserver collisionObserver;

    public void setCollisionObserver(CollisionObserver observer) {
        this.collisionObserver = observer;
    }

    @Override
    public void awake() {
        collider = node().get(Collider.class);
    }

    @Override
    public void update() {
        if (!active())
            return;


        float mag = velo.mag();
        float drag = (linDrag + aLinDrag * mag) * conductor.delta();
        float newMag = Math.max(mag - drag, 0);

        velo = velo.normal(mag).mult(newMag);


        Vec2 pos = transform().pos();
        transform().setPos(pos.add(velo.mult(conductor.delta())));

        backTrack();
    }

    private float stepMult = 0.25f;

    private void backTrack() {
        if (collider == null)
            return;

        OverlapCircle overlap = getOverlapCircle();
        if (overlap.collisions().size() == 0) {
            return;
        }
        Node other = overlap.collisions().get(0);

        for (int i = 0; i < 4 && overlap.collisions().size() > 0; i++) {
            transform().setPos(transform().pos()
                    .add(velo.mult(-conductor.delta() * stepMult)));


            overlap = getOverlapCircle();
        }

        if (overlap.collisions().size() == 0) {
            Vec2 tan = other.transform().pos().sub(transform().pos()).normal();

            velo = velo.flip(tan).mult(-bounceF);
        }

        if (collisionObserver != null)
            collisionObserver.onCollisionEnter(other);
    }

    private OverlapCircle getOverlapCircle() {
        float r = collider.scaledSize().x / 2;
        Vec2 center = collider.currentCenterPos();

        OverlapCircle overlapCircle = new OverlapCircle(r, center, collider.theyAre(), conductor);
        return overlapCircle;
    }
}
