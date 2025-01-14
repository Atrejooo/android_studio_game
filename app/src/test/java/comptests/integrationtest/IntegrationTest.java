package comptests.integrationtest;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import connector.ConnectorFactory;
import gameframe.Game;
import gameframe.functionalities.movement.TestVelocityMovement;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.Vec2;

public class IntegrationTest {

    Game game1;
    Game game2;

    TestScene testScene1, testScene2;

    @BeforeEach
    public void before() {
        ConnectorFactory.insertMock = true;

        testScene1 = new TestScene(true);
        testScene2 = new TestScene(false);

        game1 = new Game(new Scene[]{testScene1}, "");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        game2 = new Game(new Scene[]{testScene2}, "");
    }


    @Test
    public void testSynchronizationSuccess() {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int size1 = testScene1.getNodes().size();
        int size2 = testScene2.getNodes().size();

        assertEquals(size1, size2);


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Node node : testScene1.getNodes()) {
            for (Node node2 : testScene2.getNodes()){
                if(node.name.equals(node2.name)){
                    Vec2 velo1 = node.get(TestVelocityMovement.class).velo();
                    Vec2 pos1 = node.transform().pos();

                    Vec2 velo2 = node2.get(TestVelocityMovement.class).velo();
                    Vec2 pos2 = node2.transform().pos();

                    float dela = 0.2f;

                    assertEquals(velo1.x, velo2.x, dela);
                    assertEquals(velo1.y, velo2.y, dela);

                    assertEquals(pos1.x, pos2.x, dela);
                    assertEquals(pos1.y , pos2.y, dela);
                }
            }
        }
    }
}
