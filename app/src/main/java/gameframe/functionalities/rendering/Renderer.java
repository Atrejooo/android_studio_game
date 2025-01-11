package gameframe.functionalities.rendering;

import common.data.RendererData;
import gameframe.conductors.Conductor;
import gameframe.nodes.*;

public abstract class Renderer extends Comp {
    public Renderer(Node node) {
        super(node);
    }

    protected RendererData data;

    public RendererData data() {
        data.copyValues(transform());
        // make sure that the translation is copied when the entire
        // update (update + lateUpdate) is done
        return data;
    }

    @Override
    public void setConductor(Conductor conductor) {
        super.setConductor(conductor);
        if (conductor != null)
            conductor.addRenderer(this);
    }

    @Override
    public void removeConductor() {
        if (conductor != null)
            conductor.removeRenderer(this);
        super.removeConductor();
    }
}
