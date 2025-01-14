package gameframe.scenes;

import java.util.ArrayList;

import gameframe.conductors.Conductible;
import gameframe.conductors.Conductor;
import gameframe.functionalities.syncing.IdableTypeLibrary;
import gameframe.nodes.Disposable;
import gameframe.nodes.Node;
import gameframe.functionalities.rendering.Camera;
import gameframe.nodes.NodeWrapper;
import gameframe.utils.Vec2;

public class Scene extends Conductible implements IdableTypeLibrary {
    protected ArrayList<Node> nodes = new ArrayList<Node>();
    private ArrayList<Disposable> disposables = new ArrayList<>();

    /**
     * Adds a node to the scene.
     *
     * @return added node
     */
    public Node add() {
        Node node = new Node();
        nodes.add(node);
        passConductor(node);
        return node;
    }

    /**
     * Adds a node to the scene.
     *
     * @param pos position of the node
     * @return added node
     */
    public Node add(Vec2 pos) {
        Node node = add();
        node.transform().setPos(pos);
        return node;
    }

    /**
     * Adds a node to the scene with a name.
     *
     * @param name of the node
     * @param pos  position of the node
     * @return added node
     */
    public Node add(Vec2 pos, String name) {
        Node node = add(pos);
        node.name = name;
        return node;
    }

    public void add(Disposable disposable) {
        if (disposable != null)
            disposables.add(disposable);
    }

    public Node addCamera() {
        Node cam = add(new Vec2(0, 0), "camera");
        cam.add(Camera.class);
        return cam;
    }

    /**
     * Initializes scene.
     *
     * @param conductor current {@code Conductor}
     */
    public void init(Conductor conductor) {
        setConductor(conductor);
    }

    /**
     * Finds and returns the fisrt {@code Node} with matching name.
     *
     * @param name of the {@code Node}
     * @return The first {@code Node} with matching name.
     */
    public Node getNode(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    public boolean removeNode(Node node) {
        if (node == null || enteredDisposing)
            return false;

        boolean result = nodes.remove(node);
        if (result) {
            node.dispose();
        }
        return result;
    }

    /**
     * Disposes scene, nodes and components.
     */
    private boolean enteredDisposing = false;

    public void dispose() {
        enteredDisposing = true;
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).dispose();
        }

        nodes.clear();

        for (int i = 0; i < disposables.size(); i++) {
            disposables.get(i).dispose();
        }

        disposables.clear();
        enteredDisposing = false;
    }

    @Override
    public Class[] getIdableTypes() {
        return new Class[0];
    }
}