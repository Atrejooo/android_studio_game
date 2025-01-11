
package gameframe.functionalities.input;

import android.util.Log;

import gameframe.nodes.*;
import gameframe.utils.Vec2;
import gameframe.utils.quadtrees.Rect;

public class RectInputReciever extends Comp {
    public RectInputReciever(Node node) {
        super(node);
    }

    public boolean pressed() {
        return pressed;
    }

    public Vec2 where() {
        return where;
    }

    private Vec2 size = new Vec2(0, 0);
    private Vec2 where = new Vec2();
    private Vec2 center = new Vec2(0, 0);
    private boolean pressed;

    public void setSize(Vec2 size) {
        this.size = size.clone();
    }

    public Vec2 relativeSize() {
        return size;
    }

    public Vec2 size() {
        return transform().scale().mult(size);
    }


    public Vec2 center() {
        return center;
    }

    public void setCenter(Vec2 center) {
        if (center != null)
            this.center = center;
    }

    public Rect rect() {
        Vec2 currentSize = size();
        Vec2 currentCornerPos = transform().pos().add(currentSize.mult(new Vec2(-center.x, center.y)));
        Rect rect = new Rect(currentCornerPos, currentSize);

        return rect;
    }

    InputObserver observer;

    public void setObserver(InputObserver observer) {
        this.observer = observer;
    }

    boolean wasPressing;

    @Override
    public void update() {
        wasPressing = pressed;
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

        Rect currentRect = rect();

        for (Vec2 input : inputs) {
            boolean contains = currentRect.contains(input);

            if (contains) {
                //Log.d("circle input", "circle was pressed");
                pressed = true;
                where = input;
                break;
            }
        }

        if (observer == null)
            return;

        if (wasPressing != pressed) {
            if (pressed) observer.onPressed();
            else observer.onRelease();
        } else if (pressed) {
            observer.onHold();
        }
        //Log.d("circle input", "is pressed: " + pressed());
    }
}
