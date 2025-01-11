package gameframe.functionalities.movement;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.AddF;

public class Wiggle extends Comp {
    public Wiggle(Node node) {
        super(node);
    }

    private boolean started;
    private float strength;
    private float speed;
    private float fallOff;

    public void set(float strength, float speed, float fallOff) {
        this.strength = strength;
        this.speed = speed;
        this.fallOff = fallOff;
    }

    public void start() {
        started = true;
    }

    @Override
    public void update() {
        if (!active() || !started)
            return;

        strength -= conductor.delta() * fallOff;
        strength = Math.max(0, strength);

        if (strength == 0) {
            started = false;
            return;
        }

        float t = (float) Math.sin(conductor.time() * speed);
        transform().roation = t * strength;
    }
}
