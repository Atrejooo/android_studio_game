package comptests.gameframetests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gameframe.Game;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.testUtils.MoveAround;

public class SceneHierachyTest {
    private TestScene testScene;
    private Game game;

    @BeforeEach
    public void before() {
        testScene = new TestScene();
        game = new Game(new Scene[]{testScene}, "");
    }

    @Test
    public void testGetNode() {
        Node node = testScene.getNode("test_node1");

        assert (node != null);
        assertEquals(node.name, "test_node1");

        node.dispose();
        node = testScene.getNode("test_node1");

        assert (node == null);
    }

    @Test
    public void testCompCreation() {
        Node node = testScene.getNode("test_node1");

        assert (node != null);

        node.add(MoveAround.class);
        MoveAround moveAround = node.get(MoveAround.class);

        assertNotNull(moveAround);
        assert (moveAround.active());

        assertEquals(moveAround.node(), node);
    }

    @Test
    public void testCompRemoving() {
        Node node = testScene.getNode("test_node1");

        assert (node != null);

        node.add(MoveAround.class);
        MoveAround moveAround = node.get(MoveAround.class);
        assertNotNull(moveAround);

        node.remove(moveAround);
        moveAround = node.get(MoveAround.class);
        assertNull(moveAround);
    }

    @Test
    public void testActiveHierachy() {
        Node node = testScene.getNode("test_node1");

        MoveAround moveAround = node.add(MoveAround.class);

        assert (moveAround.active());
        node.setActive(false);

        assert (!moveAround.active());
        assertEquals(node.isActive(), moveAround.active());
    }

    @Test
    public void disposeSceneTest() {
        Node node = testScene.getNode("test_node");
        Node node1 = testScene.getNode("test_node1");
        Node node2 = testScene.getNode("test_node2");
        Node controlNode = testScene.getNode("test");

        assertNull(controlNode);
        assertNotNull(node);
        assertNotNull(node1);
        assertNotNull(node2);

        testScene.dispose();

        node = testScene.getNode("test_node");
        node1 = testScene.getNode("test_node1");
        node2 = testScene.getNode("test_node2");

        assertNull(node);
        assertNull(node1);
        assertNull(node2);
    }


    @Test
    public void testSceneInit() {
        Node node = testScene.getNode("test_node");
        Node node1 = testScene.getNode("test_node1");
        Node node2 = testScene.getNode("test_node2");
        Node controlNode = testScene.getNode("test");

        assertNull(controlNode);
        assertNotNull(node);
        assertNotNull(node1);
        assertNotNull(node2);

        testScene.dispose();

        node = testScene.getNode("test_node");
        node1 = testScene.getNode("test_node1");
        node2 = testScene.getNode("test_node2");

        assertNull(node);
        assertNull(node1);
        assertNull(node2);

        testScene.init(testScene.getConductor());

        node = testScene.getNode("test_node");
        node1 = testScene.getNode("test_node1");
        node2 = testScene.getNode("test_node2");
        controlNode = testScene.getNode("test");

        assertNull(controlNode);
        assertNotNull(node);
        assertNotNull(node1);
        assertNotNull(node2);

    }
}
