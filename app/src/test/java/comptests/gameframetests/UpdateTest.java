
package comptests.gameframetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gameframe.Game;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;


/**
 * Tests if the updates of the Game loop are being correctly called
 */
public class UpdateTest {
    private TestScene testScene;
    private Game game;
    private Node testNode;

    static boolean calledAwake = false;
    static boolean calledUpdate = false;
    static int updateCalls;

    @BeforeEach
    public void before() {
        testScene = new TestScene();
        calledUpdate = false;
        calledAwake = false;
        updateCalls = 0;
        game = new Game(new Scene[]{testScene}, "");

        testNode = testScene.getNode("test_node");
    }

    @Test
    public void updateTest() {
        testNode.add(TestComp.class);
        assert (calledAwake);
        assert (!calledUpdate);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assert (calledUpdate);
    }


    @Test
    public void updateOnNorActive() {
        testNode.add(TestComp.class);
        assert (calledAwake);
        assert (!calledUpdate);
        int updateCallsBefore = updateCalls;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assert (updateCallsBefore < updateCalls);
        assert (calledUpdate);
        calledUpdate = false;

        testNode.setActive(false);

        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        calledUpdate = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assert (!calledUpdate);
    }

    @Test
    public void updateCountTest() {
        testNode.add(TestComp.class);
        assert (calledAwake);
        assert (!calledUpdate);
        int updateCallsBefore = updateCalls;

        //set to 10 updates per second
        testNode.getConductor().setFps(10);
        try {
            Thread.sleep(220);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //should have happened 2 updates
        assert (updateCalls - updateCallsBefore == 2);
        assert (calledUpdate);
        calledUpdate = false;

        testNode.setActive(false);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assert (!calledUpdate);
    }

}