package gameframe.conductors;

import java.util.ArrayList;

import gameframe.looping.*;

/**
 * The {@code UpdateManager} class manages and calls updates for each
 * {@code Updateable} object.
 * It implements the {@code Loopable} interface, allowing it to participate in
 * the update loop.
 */
public class UpdateManager implements Loopable {

    private ArrayList<Updateable> updateables = new ArrayList<Updateable>();
    private DoneUpdateable[] doneUpdateables;

    public void setDoneUpdateables(DoneUpdateable[] doneUpdateables) {
        if (doneUpdateables != null)
            this.doneUpdateables = doneUpdateables;
    }

    /**
     * Adds an {@code Updateable} object to be managed by this
     * {@code UpdateManager}.
     *
     * @param updateable The {@code Updateable} object to add.
     */
    public void add(Updateable updateable) {
        if (!updateables.contains(updateable))
            updateables.add(updateable);
    }

    /**
     * Removes an {@code Updateable} object from this {@code UpdateManager}.
     *
     * @param updateable The {@code Updateable} object to remove.
     */
    public boolean remove(Updateable updateable) {
        if (enteredUpdateLoop) {
            if (updateables.contains(updateable)) {
                removeQueue.add(updateable);
                return true;
            }
            return false;
        } else
            return updateables.remove(updateable);
    }

    private ArrayList<Updateable> removeQueue = new ArrayList<Updateable>();

    /**
     * Calls the {@code update()} method on each managed {@code Updateable} object.
     * This method is invoked during each tick of the main game loop ({@code Loop}).
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     * @param loopTime  The current time in seconds since the loop started.
     */
    private boolean enteredUpdateLoop;

    @Override
    public void tick(float deltaTime, float loopTime) {
        if (removeQueue.size() > 0) {
            for (int i = 0; i < removeQueue.size(); i++) {
                updateables.remove(removeQueue.get(i));
            }
            removeQueue.clear();
        }

        enteredUpdateLoop = true;

        //initial update
        for (int i = 0; i < updateables.size(); i++) {
            Updateable updateable = updateables.get(i);
            if (updateable != null)
                updateable.update();
        }

        //late update
        for (int i = 0; i < updateables.size(); i++) {
            Updateable updateable = updateables.get(i);
            if (updateable != null)
                updateable.lateUpdate();
        }

        //update for done listeners
        for (int i = 0; i < doneUpdateables.length; i++) {
            DoneUpdateable updateable = doneUpdateables[i];
            if (updateable != null)
                updateable.doneUpdating();
        }

        enteredUpdateLoop = false;
    }
}
