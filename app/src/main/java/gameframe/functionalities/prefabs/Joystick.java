package gameframe.functionalities.prefabs;

import common.data.ImgRendererData;
import gameframe.conductors.Conductor;
import gameframe.functionalities.input.JoystickReciever;
import gameframe.functionalities.movement.CameraAnchor;
import gameframe.functionalities.rendering.ImgRenderer;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.nodes.Node;
import gameframe.utils.Color;
import gameframe.utils.Vec2;

public class Joystick {
    private static Color color = new Color(0.4f, 0.3f, 1f, 0.2f);

    public static Node create(float radius, Vec2 anchorPos, float handleFactor, Conductor conductor) {
        if (conductor == null)
            return null;

        Node joystickBase = conductor.activeScene().add(new Vec2(), "joystick_base");
        ImgRenderer renderer = (ImgRenderer) joystickBase.add(ImgRenderer.class);

        ImgRendererData renData = renderer.data();

        renData.setImg("whiteCircle", 0);
        renData.setColor(color);
        renData.setPxPerUnit(100);
        renData.layerGroup = LayerGroup.UI;

        CameraAnchor anchor = (CameraAnchor) joystickBase.add(CameraAnchor.class);
        anchor.setAnchor(anchorPos);
        anchor.setScale(new Vec2(radius, radius));

        JoystickReciever joystickReciever = (JoystickReciever) joystickBase.add(JoystickReciever.class);
        joystickReciever.setRelativeRadius(3f);
        joystickReciever.setMaxZone(1 / 3f);

        //handle-----
        Node joystickHandle = conductor.activeScene().add(new Vec2(), "joystick_base");
        renderer = (ImgRenderer) joystickHandle.add(ImgRenderer.class);

        renData = renderer.data();

        renData.setImg("whiteCircle", 0);
        renData.setColor(color);
        renData.setPxPerUnit(100);
        renData.layerGroup = LayerGroup.UI;
        renData.layer = 1;

        anchor = (CameraAnchor) joystickHandle.add(CameraAnchor.class);
        anchor.setAnchor(anchorPos);
        anchor.setScale(new Vec2(radius * handleFactor, radius * handleFactor));


        joystickReciever.setHandle(anchor);

        return joystickBase;
    }
}
