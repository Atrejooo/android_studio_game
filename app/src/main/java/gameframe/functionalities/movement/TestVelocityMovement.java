package gameframe.functionalities.movement;

import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.nodes.Node;
import gameframe.utils.Color;
import gameframe.utils.Delay;
import gameframe.utils.Vec2;
import singeltons.Randoms;

public class TestVelocityMovement extends VelocityBounceMovement {
    public TestVelocityMovement(Node node) {
        super(node);
    }

    Delay delay = new Delay(2);

    @Override
    public void update() {
        if (!delay.ready(conductor.delta())) {
            int minus = 1;
            if (delay.progress() > 0.6)
                minus = -1;
            Vec2 dir = Vec2.fromAngle((delay.progress() + (delay.progress() * 0.3f + 0.1f)) * 3 * minus);
            addForce(dir.mult(conductor.delta() * 30));
        }
        super.update();
//        if (Randoms.range01() > 0.7f)
//            node().get(ImgRenderer.class).data().setColor(new Color(Randoms.range01(), Randoms.range01(), Randoms.range01(), 1));
    }
}
