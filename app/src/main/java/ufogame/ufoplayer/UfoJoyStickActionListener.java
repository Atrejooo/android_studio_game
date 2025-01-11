package ufogame.ufoplayer;

import gameframe.functionalities.input.JoystickReciever;
import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;
import synchronizer.ActionPackage;

public class UfoJoyStickActionListener extends Comp {
    public UfoJoyStickActionListener(Node node) {
        super(node);
    }

    private Vec2 lastSignificantDir = new Vec2(99, 99);
    private final float angleThreshold = 0.1f;
    private final float magThreshold = 0.1f;

    @Override
    public void update() {
        if (!active() || joystickReciever == null)
            return;

        Vec2 dir = joystickReciever.dir();
        //dir = new Vec2(1, 0);

        float angleDiff = Math.abs(dir.atan() - lastSignificantDir.atan());
        float magDiff = Math.abs(dir.mag() - lastSignificantDir.mag());

        if (angleDiff > angleThreshold || magDiff > magThreshold) {
            ActionPackage current = conductor.getActionPackage();
            if (current != null) {
                if (current instanceof UfoActionPackage) {
                    UfoActionPackage ufoActionPackage = (UfoActionPackage) current;

                    ufoActionPackage.inputDir = dir;
                    lastSignificantDir = dir;

                    conductor.setActionPackage(ufoActionPackage);
                }
            } else {
                UfoActionPackage ufoActionPackage = new UfoActionPackage(conductor.getMyPlayerId());

                ufoActionPackage.inputDir = dir;

                lastSignificantDir = dir;

                conductor.setActionPackage(ufoActionPackage);
            }
        }
    }

    private void set(Vec2 dir) {

    }

    private JoystickReciever joystickReciever;

    @Override
    public void awake() {
        joystickReciever = node().get(JoystickReciever.class);
    }
}
