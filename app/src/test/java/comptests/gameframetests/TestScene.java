package comptests.gameframetests;

import gameframe.conductors.Conductor;
import gameframe.scenes.Scene;
import gameframe.utils.Vec2;

public class TestScene extends Scene {
    @Override
    public void init(Conductor conductor) {
        super.init(conductor);


        add(new Vec2(),"test_node");
        add(new Vec2(),"test_node1");
        add(new Vec2(),"test_node2");
    }
}
