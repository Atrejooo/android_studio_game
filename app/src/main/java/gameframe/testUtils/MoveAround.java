package gameframe.testUtils;

import gameframe.nodes.*;
import gameframe.utils.AddF;
import singeltons.Randoms;
import gameframe.utils.Range;
import gameframe.utils.Vec2;

public class MoveAround extends Comp {
    public MoveAround(Node node) {
        super(node);
    }

    @Override
    public void awake() {
        float size = 3f;
        node().transform().setPos(
                new Vec2(size * Randoms.range(-1f, 1f), size * Randoms.range(-1f, 1f))
        );

        rotationSpeed = Randoms.range01() * AddF.PI * 2;
    }

    float rotationSpeed = 0;
    float angle;

    @Override
    public void update() {
//        if (Randoms.range01() > 0.99) {
//            float size = 14f;
//            node().transform().setPos(
//                    new Vec2(size * Randoms.range(-1f, 1f), size * Randoms.range(-1f, 1f))
//            );
//        }
//
//        float value = new Range(0.4f, 1.5f).lerp((float) Math.sin(conductor.time()) * 0.5f + 0.5f);
        //Log.i("random_moving", "moved");
//        node().transform().setScale(new Vec2(value, 1-value * 0.5f));
//        //node().transform().roation += conductor.delta() * 8;


        if (Randoms.range01() > 0.9) {
            if (rotationSpeed > 0) rotationSpeed -= Randoms.range01();
            else rotationSpeed += Randoms.range01();
        }

        angle += conductor.delta() * rotationSpeed * 10f;

        Vec2 dir = Vec2.fromAngle(angle);

        transform().setPos(transform().pos().add(dir.mult(conductor.delta() * 5f)));
    }
}
