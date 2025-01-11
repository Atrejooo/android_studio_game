package gameframe.conductors;

public class Conductible {
    protected Conductor conductor;

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public boolean passConductor(Conductible conductible) {
        if (conductor != null) {
            conductible.setConductor(conductor);
            return true;
        } else
            return false;
    }

    public void removeConductor() {
        conductor = null;
    }
}