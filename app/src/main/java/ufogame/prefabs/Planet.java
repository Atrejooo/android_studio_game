package ufogame.prefabs;

import common.data.ImgRendererData;
import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.movement.VelocityBounceMovement;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.Color;
import gameframe.utils.Vec2;

public class Planet {
    public static Node createPlanet(Scene scene, Vec2 pos, int imgNumber) {
        Node node = scene.add(pos, "planet");
        node.transform().setScale(new Vec2(10));

        Collider collider = node.add(Collider.class);
        collider.setCircle(1);
        collider.setTheyAre(0b0000000000000000);
        collider.setThisIs(0b0000000000000001);

        ImgRenderer renderer = node.add(ImgRenderer.class);
        ImgRendererData rendererData = renderer.data();
        rendererData.setPxPerUnit(1000);
        rendererData.setCenter(new Vec2(0.5f, 0.5f));
        rendererData.setImg("planet", imgNumber);
        rendererData.setColor(new Color(1));

        return node;
    }
}
