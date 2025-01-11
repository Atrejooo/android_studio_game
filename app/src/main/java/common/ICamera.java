package common;

import gameframe.utils.Vec2;

public interface ICamera {
    float sizeX();

    Vec2 map(Vec2 point, float aspect);

    Vec2 world2ScreenPoint(Vec2 worldPos, Vec2 screen);

    Vec2 screen2WorldPoint(Vec2 screenPos, Vec2 screen);
}
