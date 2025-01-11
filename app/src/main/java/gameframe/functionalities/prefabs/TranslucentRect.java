package gameframe.functionalities.prefabs;

import gameframe.conductors.Conductor;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.utils.Color;
import gameframe.utils.Vec2;
import gameframe.nodes.*;
import gameframe.utils.quadtrees.Rect;

public class TranslucentRect {
    public static Node create(Rect rect, Conductor conductor) {
        return create(rect, new Color(1, 0, 1, 0.5f), conductor);
    }

    public static Node create(Rect rect, Color color, Conductor conductor) {
        Node node = conductor.activeScene().add(rect.pos, "show rect");
        ImgRenderer renderer = (ImgRenderer) node.add(ImgRenderer.class);
        renderer.data().setCenter(new Vec2());
        renderer.data().setPxPerUnit(100);
        renderer.data().setImg("square", 0);
        renderer.data().setColor(color);
        renderer.data().layerGroup = LayerGroup.UI;
        node.transform().setScale(rect.size);

        return node;
    }
}
