package gameframe.functionalities.movement;

import android.util.Log;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class VelocityMovement extends Comp {
    public VelocityMovement(Node node) {
        super(node);
    }

    protected Vec2 velo = new Vec2(0);
    protected float linDrag = 0.2f;
    protected float aLinDrag;

    public float getLinDrag() {
        return linDrag;
    }

    public void setLinDrag(float linDrag) {
        if (linDrag < 0)
            return;
        this.linDrag = linDrag;
    }

    public float getaLinDrag() {
        return aLinDrag;
    }

    public void setaLinDrag(float aLinDrag) {
        if (aLinDrag < 0)
            return;
        this.aLinDrag = aLinDrag;
    }

    public Vec2 velo() {
        return velo.clone();
    }

    public void setVelo(Vec2 velo) {
        if (velo != null)
            this.velo = velo.clone();
    }

    public void addForce(Vec2 force) {
        velo = velo.add(force);
    }

    @Override
    public void update() {
        if (!active())
            return;

        float mag = velo.mag();
        float newMag = (float) Math.max(mag - (linDrag + aLinDrag * mag) * conductor.delta(), 0);

        velo = velo.normal().mult(newMag);


        Vec2 pos = transform().pos();
        transform().setPos(pos.add(velo.mult(conductor.delta())));
    }
}
