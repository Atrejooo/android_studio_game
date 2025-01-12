package gameframe.nodes;

import java.util.LinkedList;
import java.util.Objects;

import gameframe.conductors.Conductible;
import gameframe.conductors.Conductor;
import gameframe.conductors.Updateable;

public abstract class NodeWrapper extends Conductible implements Disposable, Updateable {
    public String name;
    public LinkedList<Node> nodes = new LinkedList<>();

    /**
     * instancieates a NodeWrapper and adds it to the active scene
     *
     * @param conductor
     */
    public NodeWrapper(Conductor conductor) {
        conductor.activeScene().add(this);
        setConductor(conductor);
    }

    /**
     * adds a node to the wrapper which gets disposed when this wrapper gets disposed.
     */
    public void addNode(Node node) throws NullPointerException {
        Objects.requireNonNull(node, "node cannot be null");
        if (!nodes.contains(node)) {
            nodes.add(node);
            node.nodeWrapper = this;
        }
    }

    @Override
    public void setConductor(Conductor conductor) {
        super.setConductor(conductor);

        if (conductor != null) {
            conductor.updateManager().add(this);
        }
    }

    @Override
    public void removeConductor() {
        if (conductor != null) {
            conductor.updateManager().remove(this);
        }

        super.removeConductor();
    }

    @Override
    public void dispose() {
        while (nodes.size() > 0) {
            Node node = nodes.removeFirst();
            node.dispose();
            node.nodeWrapper = null;
        }
        removeConductor();
    }

    public void disposeNodes(){
        while (nodes.size() > 0) {
            Node node = nodes.removeFirst();
            node.dispose();
            node.nodeWrapper = null;
        }
    }

    public boolean active() {
        return conductor != null;
    }

    public <CompType extends Comp> CompType getInNodes(Class<CompType> compClass) {
        for (Node node : nodes) {
            CompType comp = node.get(compClass);

            if (comp != null)
                return comp;
        }
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void lateUpdate() {

    }
}
