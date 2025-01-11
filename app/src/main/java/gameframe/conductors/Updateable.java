package gameframe.conductors;

/**
 * Something that can be updated by an {@code UpdateManager}
 */
public interface Updateable {
    void update();

    /**
     * called after {@code update()} was invoked on each {@code Updateable}
     */
    void lateUpdate();
}
