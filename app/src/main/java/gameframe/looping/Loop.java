package gameframe.looping;

import singeltons.PerformanceTester;

/**
 * The {@code Loop} class represents a game or simulation loop that executes
 * a series of updateable objects (implementing the {@link Loopable} interface).
 * The loop runs at a specified frame rate (fps) and invokes the {@code tick()}
 * method
 * on each {@code Loopable} object.
 */
public class Loop implements Runnable {
    private float fps = 30;
    private Thread thread;
    private boolean go;

    private int maxDelay;
    private long startTime = 0;
    private int updateTime;
    private int loopTime;
    private int deltaTime;

    private Loopable[] loopables;

    /**
     * Constructs a {@code Loop} object with an array of {@code Loopable} objects to
     * update.
     *
     * @param loopables An array of {@code Loopable} objects to be updated in each
     *                  loop iteration.
     */
    public Loop(Loopable[] loopables) {
        this.loopables = loopables;
    }

    /**
     * Starts the loop by initializing the thread and setting the start time.
     */
    public void start() {
        go = true;
        startTime = getTime();
        thread = new Thread(this);
        if (!thread.isAlive())
            thread.start();
    }

    public void stop() {
        go = false;
    }

    /**
     * Sets the fps of this.
     *
     * @param fps or updates per second
     * @throws IllegalArgumentException
     */
    public void setFps(int fps) throws IllegalArgumentException {
        if (fps > 0) {
            this.fps = fps;
            maxDelay = Math.round(1000f / fps);
        } else
            throw new IllegalArgumentException("Fps needs to be > 0.");
    }

    /**
     * It calculates the time difference between frames and invokes the
     * {@code tick()} method
     * on the {@code Loopable} objects, maintaining the desired frame rate (fps).
     */
    @Override
    public void run() {
        while (thread != null && go) {
            // update Time
            loopTime = getTime();
            deltaTime = loopTime - updateTime;
            updateTime = loopTime;

            tick();

            try {
                int sleep = maxDelay - (getTime() - updateTime);
                if (sleep > 0)
                    Thread.sleep(sleep);

                PerformanceTester.passTimeData(loopTime, sleep, maxDelay);
                //System.out.println("waiting for: " + sleep + ">");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates all {@code Loopable} objects by invoking their {@code tick()} method.
     * The time difference between frames (deltaTime) and the current loop time
     * (loopTime)
     * are passed as arguments to each {@code Loopable}.
     */
    private void tick() {
        for (Loopable loopable : loopables) {
            loopable.tick(deltaTime / 1000f, loopTime / 1000f);
        }
    }

    /**
     * Returns the current time in seconds since the loop started.
     * The time is calculated based on the system's nanosecond time.
     *
     * @return The elapsed time in seconds since the loop started.
     */
    public int getTime() {
        return (int) (System.nanoTime() / 1000000L - startTime);
    }
}
