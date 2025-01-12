package gameframe.nodes;

import java.util.ArrayList;

import gameframe.conductors.Conductible;

/**
 * <b>Node</b><br>
 * <br>
 * A {@code Node} is an Object that is bound to a scene. It is a representation
 * for a thing inside a scene.
 */
public class Node extends Conductible implements Disposable {
    public String name = "node";
    private boolean dead = false;
    private boolean active = true;

    private Transform transform = new Transform();

    public Transform transform() {
        return transform;
    }

    public NodeWrapper getNodeWrapper() {
        return nodeWrapper;
    }

    NodeWrapper nodeWrapper;

    private ArrayList<Comp> components = new ArrayList<Comp>();

    /**
     * Adds a component ({@code Comp}) of the specified type to this {@code Node}
     * and initializes it by calling its {@code awake()} method.
     * </p>
     *
     * @param compClass The {@code Class} object representing the component type to
     *                  be added.
     *                  The component type must extend {@code Comp} and have a
     *                  constructor that
     *                  takes a {@code Node} as a parameter.
     * @return Comp instance or null if the node was disposed of
     */
    public <CompType extends Comp> CompType add(Class<CompType> compClass) {
        if (dead)
            return null;
        try {
            CompType comp = compClass.getConstructor(Node.class).newInstance(this);
            passConductor(comp);
            components.add(comp);
            comp.setNode(this);
            comp.awake();
            return comp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void add(Comp comp) {
        if (comp != null) {
            passConductor(comp);
            comp.awake();
            comp.setNode(this);
            components.add(comp);
        }
    }

    /**
     * Retruns the first occurance of the specefied {@code CompType} which extends
     * {@code Comp}
     *
     * @param <CompType> extends {@code Comp}
     * @param compClass  the type of component as {@code "Comp".class}
     * @return the fist instance of the specefied type or <b>null</b> if the
     * component was not found
     */
    public <CompType extends Comp> CompType get(Class<CompType> compClass) {
        for (Comp comp : components) {
            if (compClass.isInstance(comp)) {
                try {
                    return compClass.cast(comp);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public void remove(Comp comp) {
        boolean done = components.remove(comp);
        if (done) {
            comp.removeConductor();
        }
    }

    /**
     * If the {@code dispose()} methode was called the node is now dead and all its
     * components were disposed of and removed.
     *
     * @return
     */
    public boolean isDead() {
        return dead;
    }

    public boolean isActive() {
        return active && !dead;
    }

    public void setActive(boolean value) {
        active = value;
        for (Comp comp : components) {
            comp.updateActiveStatus();
        }
    }

    @Override
    public void dispose() {
        if (dead)
            return;

        for (Comp comp : components) {
            comp.removeConductor();
        }
        components.clear();
        dead = true;

        if (conductor == null)
            return;

        conductor.activeScene().removeNode(this);

        removeConductor();
    }


    public String toString() {
        return name;
    }
}
