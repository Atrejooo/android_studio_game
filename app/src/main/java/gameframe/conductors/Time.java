package gameframe.conductors;

import gameframe.looping.Loopable;

/**
 * Represents the games time.
 */
public class Time implements Loopable {
    private float deltaTime;
    private float gameTime;
    private float timeScale = 1f;
    private float unscaledTime;


    public float unscaledTime() {
        return unscaledTime;
    }

    /**
     * Returns the time elapsed since the last tick.
     *
     * @return The delta time in seconds.
     */
    public float delta() {
        return deltaTime;
        //return 0.025f;
    }

    /**
     * Returns the total time elapsed since the start.
     *
     * @return The total game time in seconds.
     */
    public float time() {
        return gameTime;
    }

    /**
     * Returns the current time scale applied to the game time.
     *
     * @return The time scale factor.
     */
    public float scale() {
        return timeScale;
    }

    /**
     * Sets the time scale for the game.
     * A time scale greater than 1 speeds up the game, while a scale less than 1
     * slows it down.
     *
     * @param newTimeScale The new time scale factor to be applied.
     */
    public void setScale(float newTimeScale) {
        timeScale = newTimeScale;
    }

    /**
     * Updates the game time and delta time based on the time elapsed since the last
     * tick.
     *
     * @param deltaTime The time elapsed since the last tick, in seconds.
     * @param loopTime  The current time in seconds since the loop started.
     */
    public void tick(float deltaTime, float loopTime) {
        unscaledTime += deltaTime;
        gameTime += deltaTime * timeScale;
        this.deltaTime = deltaTime * timeScale;
    }
}
