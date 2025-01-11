package ufogame.scenes;


import gameframe.functionalities.rendering.LayerGroup;
import gameframe.functionalities.syncing.DisposeSyncedEvent;
import ufogame.ball.BallWrapper;
import ufogame.ufoplayer.UfoJoyStickActionListener;
import ufogame.ufoplayer.UfoPlayerFactory;
import ufogame.ufoplayer.UfoPlayerWrapper;
import common.data.TxtRendererData;
import gameframe.conductors.Conductor;
import gameframe.functionalities.movement.CameraAnchor;
import gameframe.functionalities.prefabs.Joystick;
import gameframe.functionalities.rendering.TxtRenderer;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.ConnectionSource;
import gameframe.utils.Vec2;
import ufogame.prefabs.Planet;

public class GameScene extends Scene {
    @Override
    public void init(Conductor conductor) {
        super.init(conductor);

        addCamera();
        conductor.camera().setSizeX(40);
        conductor.startSyncing(new UfoPlayerFactory());

        addCodeText();

        if (conductor.synchronizer().isEnforcing()) {
            BallWrapper ballWrapper = new BallWrapper(conductor);
            ballWrapper.setTarget(
                    ((UfoPlayerWrapper) conductor.playerManager().getPlayerInstance(conductor.getMyPlayerId())).instanceId());
        }

        joystick = Joystick.create(1f, new Vec2(0.15f, 0.7f), 0.3f, conductor);

        joystick.add(UfoJoyStickActionListener.class);

        float planetDistance = 17;
        Planet.createPlanet(this, new Vec2(0, planetDistance), 0);
        Planet.createPlanet(this, new Vec2(planetDistance, 0), 1);
        Planet.createPlanet(this, new Vec2(-planetDistance, 0), 2);
        Planet.createPlanet(this, new Vec2(0, -planetDistance), 1);

    }

    private Node joystick;

    private void addCodeText() {
        ConnectionSource source = conductor.getConnectionSource();
        if (source.open) {
            Node node = add(new Vec2(), "connection_code_txt");
            TxtRendererData rendererData = node.add(TxtRenderer.class).data();
            rendererData.setText(source.toNumericString());
            rendererData.setCenter(new Vec2(1, 0));
            rendererData.setTextSize(30);
            rendererData.layerGroup = LayerGroup.UI;

            CameraAnchor anchor = node.add(CameraAnchor.class);
            anchor.setAnchor(new Vec2(1f, 0f));
            anchor.setScale(new Vec2(0.35f));
        }
    }

    @Override
    public Class[] getIdableTypes() {
        return new Class[]{
                UfoPlayerWrapper.class,
                DisposeSyncedEvent.class,
                BallWrapper.class
        };
    }
}
