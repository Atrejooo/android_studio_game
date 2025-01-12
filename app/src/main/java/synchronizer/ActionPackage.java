package synchronizer;

import java.io.Serializable;
import java.util.Objects;

/**
 * needs to be serializable
 */
public class ActionPackage implements Serializable {
    private int playerId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ActionPackage)) return false;
        ActionPackage that = (ActionPackage) object;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerId);
    }

    public int playerId() {
        return playerId;
    }

    public ActionPackage(int playerId) {
        this.playerId = playerId;
    }

    /**
     * resets triggers and stuff on send
     */
    public void resetOnSend(){

    }

    @Override
    public String toString() {
        return "ActionPackage: " + playerId;
    }
}
