package ufogame.prefabs;

import common.data.ImgRendererData;
import gameframe.conductors.Conductor;
import gameframe.functionalities.animation.FadeSize;
import gameframe.functionalities.rendering.FadeAndDie;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.Color;
import gameframe.utils.Range;
import gameframe.utils.Vec2;
import visual.activities.R;

public class Shockwave {
    private static Color color = new Color(0.65f, 0.65f, 1f, 0.7f);

    public static Node createShockwave(Conductor conductor, Vec2 pos, float size, float time) {
        Scene scene = conductor.activeScene();
        Node node = scene.add(pos, "shockwave");
        ImgRendererData rendererData = node.add(ImgRenderer.class).data();
        rendererData.setImg("whiteCircle", 0);
        rendererData.setCenter(new Vec2(0.5f));
        rendererData.setPxPerUnit(100);
        rendererData.layer = -1;

        node.add(FadeAndDie.class).start(color, time, 0, new Range(0.7f,0));
        node.add(FadeSize.class).start(time, new Vec2(), new Vec2(size));

        return node;
    }
}
