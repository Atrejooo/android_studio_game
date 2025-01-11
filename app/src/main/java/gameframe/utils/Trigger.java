package gameframe.utils;

import java.io.Serializable;
import java.util.Objects;

public class Trigger implements Serializable {
    private boolean usable;

    public Trigger(boolean usable) {
        this.usable = usable;
    }

    public void reset() {
        usable = true;
    }

    public boolean use() {
        boolean remember = usable;
        usable = false;
        return remember;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Trigger)) return false;
        Trigger trigger = (Trigger) object;
        return usable == trigger.usable;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(usable);
    }
}
