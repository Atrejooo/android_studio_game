package ufogame.scenes;


import android.util.Log;

import common.data.ImgRendererData;
import gameframe.functionalities.input.CircleInputReciever;
import gameframe.functionalities.input.InputObserver;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.functionalities.syncing.DisposeSyncedEvent;
import gameframe.utils.Color;
import synchronizer.ActionPackage;
import ufogame.ball.BallWrapper;
import ufogame.ufoplayer.UfoActionPackage;
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
import ufogame.ufoplayer.UfoShockWaveSyncedEvent;

public class GameScene extends Scene {
    @Override
    public void init(Conductor conductor) {
        super.init(conductor);

        addCamera();
        conductor.camera().setSizeX(40);
        conductor.startSyncing(new UfoPlayerFactory());

        addCodeText();
        addShockWaveBtn();
        if (conductor.synchronizer().isEnforcing()) {
            BallWrapper ballWrapper = new BallWrapper(conductor);
            ballWrapper.setTarget(conductor.playerManager().getPlayerInstance(conductor.getMyPlayerId()));
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

    private static Color color = new Color(0.4f, 0.3f, 1f, 0.2f);

    private void addShockWaveBtn() {
        Node node = add(new Vec2(), "connection_code_txt");
        ImgRendererData rendererData = node.add(ImgRenderer.class).data();
        rendererData.setImg("whiteCircle", 0);
        rendererData.setCenter(new Vec2(0.5f));
        rendererData.setColor(color);
        rendererData.layerGroup = LayerGroup.UI;

        CameraAnchor anchor = node.add(CameraAnchor.class);
        anchor.setAnchor(new Vec2(0.9f, 0.8f));
        anchor.setScale(new Vec2(0.35f));

        CircleInputReciever circleInputReciever = node.add(CircleInputReciever.class);
        circleInputReciever.setRelativeRadius(2);
        circleInputReciever.setObserver(new InputObserver() {
            @Override
            public void onPressed() {
                ActionPackage actionPackage = conductor.getActionPackage();
                if (actionPackage != null && actionPackage instanceof UfoActionPackage) {
                    UfoActionPackage ufoActionPackage = (UfoActionPackage) actionPackage;

                    ufoActionPackage.getPerformShockwave().reset();

                    conductor.setActionPackage(ufoActionPackage);

                    Log.d("GameScene", "ufo shockwave");
                }
            }

            @Override
            public void onRelease() {

            }

            @Override
            public void onHold() {

            }
        });
    }


    @Override
    public Class[] getIdableTypes() {
        return new Class[]{
                UfoPlayerWrapper.class,
                DisposeSyncedEvent.class,
                BallWrapper.class,
                UfoShockWaveSyncedEvent.class
        };
    }
}
