package ufogame.ufoplayer;

import java.io.Serializable;
import java.util.Objects;

import gameframe.utils.Trigger;
import gameframe.utils.Vec2;
import synchronizer.ActionPackage;

public class UfoActionPackage extends ActionPackage implements Serializable {
    public UfoActionPackage(int playerId) {
        super(playerId);
    }

    Vec2 inputDir = new Vec2();

    Trigger performShockwave = new Trigger(false);


    public Trigger getPerformShockwave() {
        return performShockwave;
    }

    /**
     * Did the input values change or are they different.
     *
     * @param other can be null
     * @return $other is different$ or $other is null$
     */
    public boolean changedInputDir(UfoActionPackage other) {
        if (other == null) {
            return true;
        }
        return inputDir.equals(other.inputDir);
    }

    @Override
    public void resetOnSend() {
        performShockwave.use();
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UfoActionPackage)) return false;
        if (!super.equals(object)) return false;
        UfoActionPackage that = (UfoActionPackage) object;
        return Objects.equals(inputDir, that.inputDir)
                && Objects.equals(performShockwave, that.performShockwave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inputDir, performShockwave);
    }
}
