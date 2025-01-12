package gameframe.functionalities.animation;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Delay;
import gameframe.utils.Vec2;

public class FadeSize extends Comp {
    public FadeSize(Node node) {
        super(node);
    }

    private Delay delay;
    private Vec2 start, end;

    private boolean started;

    public void start(float time, Vec2 start, Vec2 end) {
        started = true;
        this.delay = new Delay(time);
        this.start = start;
        this.end = end;
    }

    @Override
    public void update() {
        if (!active() || !started)
            return;
        if (!delay.ready(conductor.delta())) {
            Vec2 newScale = start.add(end.sub(start).mult(delay.progress()));
            transform().setScale(newScale);
        } else {
            transform().setScale(end);
        }
    }
}
