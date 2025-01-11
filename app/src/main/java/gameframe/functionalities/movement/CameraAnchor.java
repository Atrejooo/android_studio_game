package gameframe.functionalities.movement;

import android.util.Log;

import gameframe.functionalities.rendering.Camera;
import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class CameraAnchor extends Comp {
    public CameraAnchor(Node node) {
        super(node);
    }


    private Vec2 anchor = new Vec2(0, 0);
    private Vec2 scale = new Vec2(1, 1);


    public Vec2 scale() {
        return scale.clone();
    }

    public Vec2 anchoredScale() {
        if (conductor != null) {
            if (conductor.camera() != null) {
                return scale.mult(conductor.camera().sizeX() * 0.1f);
            }
        }
        return scale();
    }

    public void setScale(Vec2 scale) {
        this.scale = scale;
        updateTransform();
    }

    public Vec2 anchor() {
        return anchor.clone();
    }

    public void setAnchor(Vec2 anchor) {
        this.anchor = anchor;
        updateTransform();
    }

    public void setWorldPos(Vec2 woldPos) {
        if (conductor != null) {
            Camera cam = conductor.camera();
            if (cam != null) {
                anchor = cam.world2UV(woldPos);
                updateTransform();
            }
        }
    }

    @Override
    public void lateUpdate() {
        updateTransform();
    }

    private void updateTransform() {
        if (!active())
            return;

        transform().setPos(conductor.camera().UV2World(anchor));
        transform().setScale(anchoredScale());
    }
}
