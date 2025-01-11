package ufogame.prefabs;

import common.data.ImgRendererData;
import gameframe.functionalities.movement.CameraAnchor;
import gameframe.functionalities.movement.Wiggle;
import gameframe.functionalities.rendering.FadeAndDie;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.Color;
import gameframe.utils.Vec2;

public class WarningSign {
    public static Node createWarningSign(Scene scene) {
        Node node = scene.add();
        ImgRenderer imgRenderer = node.add(ImgRenderer.class);
        ImgRendererData rendererData = imgRenderer.data();
        rendererData.setImg("warning", 0);
        rendererData.setPxPerUnit(300);
        rendererData.layerGroup = LayerGroup.UI;
        rendererData.layer = 100;

        node.add(CameraAnchor.class).setAnchor(new Vec2(0.5f));

        node.add(FadeAndDie.class).start(new Color(1f, 1f, 1f, 1f), 0.4f, 0.7f);

        Wiggle wiggle = node.add(Wiggle.class);
        wiggle.set(0.4f, 18, 0.7f);
        wiggle.start();
        return node;
    }
}
