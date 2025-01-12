package gameframe.conductors;

public class Conductible implements YellListener {
    protected Conductor conductor;

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
        conductor.addYellListener(this);
    }

    public boolean passConductor(Conductible conductible) {
        if (conductor != null) {
            conductible.setConductor(conductor);
            return true;
        } else
            return false;
    }

    public void removeConductor() {
        conductor.removeYellListender(this);
        conductor = null;
    }

    @Override
    public void onYell(Yell yell) {

    }
}