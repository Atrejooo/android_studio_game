package gameframe.functionalities.input;

import gameframe.functionalities.movement.CameraAnchor;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class JoystickReciever extends CircleInputReciever {
    public JoystickReciever(Node node) {
        super(node);
    }

    private CameraAnchor handle;
    private CameraAnchor myAnchor;
    private Vec2 dir = new Vec2(0, 0);
    private float maxZone = 0.75f;

    public Vec2 dir() {
        return dir.clone();
    }

    public void setHandle(CameraAnchor handle) {
        this.handle = handle;
    }

    public void setMaxZone(float maxZone) {
        if (maxZone > 0)
            this.maxZone = maxZone;
    }

    private float maxZone() {
        return maxZone;
    }

    @Override
    public void awake() {
        myAnchor = (CameraAnchor) node().get(CameraAnchor.class);
    }

    @Override
    public void lateUpdate() {
        if (!active())
            return;

        if (handle == null)
            return;

        dir = new Vec2();

        if (pressed()) {
            float currentMaxZone = maxZone * radius();
            Vec2 offset = where().sub(transform().pos()).clampMag(0, currentMaxZone);

            dir = offset.div(currentMaxZone);

            handle.setWorldPos(transform().pos().add(offset));
        } else if (myAnchor != null) {
            handle.setAnchor(myAnchor.anchor());
        } else {
            handle.setWorldPos(transform().pos());
        }
    }
}
