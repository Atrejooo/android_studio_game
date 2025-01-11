package gameframe.functionalities.input;

import android.util.Log;

import gameframe.nodes.*;
import gameframe.utils.Vec2;

public class CircleInputReciever extends Comp {
    public CircleInputReciever(Node node) {
        super(node);
    }

    public boolean pressed() {
        return pressed;
    }

    public Vec2 where() {
        return where;
    }

    private float radius = 1f;
    private Vec2 where = new Vec2();
    private boolean pressed;

    public void setRelativeRadius(float radius) {
        this.radius = radius;
    }

    public float relativeRadius() {
        return radius;
    }

    public float radius() {
        return transform().scale().max() * radius;
    }

    @Override
    public void update() {
        pressed = false;

        if (!active())
            return;

        if (conductor == null)
            return;

        if (conductor.input() == null)
            return;

        Vec2[] inputs = conductor.input().points();

        if (inputs == null)
            return;

        for (Vec2 input : inputs) {
            float distance = input.distance(transform().pos());

            float currentRadius = radius();
            //Log.d("circle input debug", "distance: " + distance + ", radius: " + currentRadius + ", scale: " + transform().scale());


            if (distance <= currentRadius) {
                //Log.d("circle input", "circle was pressed");
                pressed = true;
                where = input.clone();
                break;
            }
        }
        //Log.d("circle input", "is pressed: " + pressed());
    }
}
