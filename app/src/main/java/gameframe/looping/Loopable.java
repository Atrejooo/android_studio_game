package gameframe.looping;

/**
 * Interface for objects attached to a {@code Loop} that have defined tick
 * behavior.
 * 
 */
public interface Loopable {
    /**
     * Method for invoking tick updates.
     * 
     * @param deltaTime is the time passed since the last tick
     * @param loopTime  is the time since the start of the loop
     */
    void tick(float deltaTime, float loopTime);
}
