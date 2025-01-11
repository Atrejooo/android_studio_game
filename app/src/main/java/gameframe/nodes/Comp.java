package gameframe.nodes;

import gameframe.conductors.*;

/**
 * <b>Component</b>
 * <br>
 * <br>
 * It represents a component that is part of a node which holds
 * functionallity and data.
 */
public class Comp extends Conductible implements Updateable {
    private Node node;
    private boolean active = true;


    public Transform transform() {
        if (node != null)
            return node.transform();
        else
            return null;
    }

    /**
     * @return the {@code Node} that this is attached to
     */
    public Node node() {
        return node;
    }

    /**
     * Sets this components node.
     *
     * @param node cannot be null
     */
    public void setNode(Node node) {
        if (node != null)
            this.node = node;
    }

    /**
     * Constructs a {@code Comp} object with the specified {@code Node}.
     *
     * @param node The {@code Node} associated with this {@code Comp}.
     */
    public Comp(Node node) {
        this.node = node;
    }

    /**
     * Sets the conductor for this {@code Comp}, removes the current conductor if
     * one exists,
     * and adds this {@code Comp} to the conductor's {@code UpdateManager}.
     *
     * @param conductor The {@code Conductor} to be assigned to this {@code Comp}.
     */
    @Override
    public void setConductor(Conductor conductor) {
        if (conductor == null)
            return;
        super.setConductor(conductor);
        conductor.updateManager().add(this);
    }

    /**
     * Removes the current conductor and ensures that this {@code Comp} is removed
     * from the conductor's {@code UpdateManager}.
     */
    @Override
    public void removeConductor() {
        active = false;
        if (conductor != null) {
            conductor.updateManager().remove(this);
        }
        super.removeConductor();
    }

    /**
     * Called when the compnent gets added to a {@code Node}.
     */
    public void awake() {
        // System.out.println("I am alive!");
    }

    /**
     * Updates the component, called on each update/frame invoked by the
     * {@code UpdateManager}.
     */
    @Override
    public void update() {
        // System.out.println("hi again ;)");
    }

    @Override
    public void lateUpdate() {

    }


    public boolean active() {
        if (node != null)
            return node.isActive() && active && conductor != null;
        else
            return false;
    }

    public void setActive(boolean value) {
        active = value;
        updateActiveStatus();
    }

    public void updateActiveStatus() {
        boolean value = active();
        if (conductor == null) return;
        if (value) {
            conductor.updateManager().add(this);
        } else {
            conductor.updateManager().remove(this);
        }
    }
}
