package gameframe.functionalities.movement;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class AnchorHover extends Comp {
    public AnchorHover(Node node) {
        super(node);
    }

    private CameraAnchor anchor;

    @Override
    public void awake() {
        anchor = node().get(CameraAnchor.class);
        if (anchor == null)
            return;
        startPos = anchor.anchor();
    }

    private Vec2 startPos;

    public float strenght = 0.015f;
    public float frequ = 0.5f;
    public float xOffset = 2;

    @Override
    public void update() {
        if (!active() || anchor == null)
            return;

        anchor.setAnchor(startPos.add(new Vec2(0, (float) Math.sin(conductor.time() * frequ + startPos.x * xOffset) * strenght)));
    }
}
