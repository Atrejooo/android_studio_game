package gameframe.testUtils;

import gameframe.conductors.Conductor;
import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.collision.ICollider;
import gameframe.functionalities.movement.TestVelocityMovement;
import gameframe.functionalities.movement.VelocityBounceMovement;
import gameframe.nodes.Node;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.scenes.Scene;
import gameframe.utils.Color;
import singeltons.Randoms;
import gameframe.utils.Vec2;

public class TestScene extends Scene {
    public Node cam;


    @Override
    public void init(Conductor conductor) {
        super.init(conductor);


        addCamera();
        conductor.camera().setSizeX(60);
        final int count = 100;
        int max = 5;
        for (int i = 0; i < max; i++) {
            float t = i / (float) max;
            Color color = new Color(t, 1 - t, 0.2f, 1);
            test(i * 400 + 100, 100, color);
        }
    }

    private void test(int delay, int count, Color color) {
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                Node node = add();
                node.transform().setScale(new Vec2(1f));
                node.add(VelocityBounceMovement.class);
                ImgRenderer rend = (ImgRenderer) node.add(ImgRenderer.class);
                rend.data().setImg("square", 0);
                rend.data().setColor(color);
                rend.data().layer = 20  * delay;
                rend.data().setPxPerUnit(100);
                VelocityBounceMovement velocityBounceMovement = node.add(TestVelocityMovement.class);
                velocityBounceMovement.setaLinDrag(0.1f);
                velocityBounceMovement.setLinDrag(20);
                velocityBounceMovement.setBounceF(0.5f);
                conductor.camera().followObj = node;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
