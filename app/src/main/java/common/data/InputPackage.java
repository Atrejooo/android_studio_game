package common.data;

import android.util.Log;

import java.util.Arrays;

import common.ICamera;
import gameframe.utils.Vec2;

public class InputPackage {
    private Vec2[] points;
    private Vec2 screen;
    private ICamera cam;

    public InputPackage(Vec2[] points, ICamera cam, Vec2 screen) {
        this.points = points;
        this.cam = cam;
        this.screen = screen;
    }

    public Vec2[] screenPoints() {
        if (points == null)
            return new Vec2[0];

        Vec2[] clone = new Vec2[points.length];

        for (int i = 0; i < points.length; i++) {
            clone[i] = points[i].clone();
        }

        return clone;
    }

    public Vec2 screenPoint(int i) {
        if (points == null) {
            Log.e("InputPackage", "points array is null");
            return null;
        }

        if (i < points.length && i > -1)
            return points[i].clone();
        else {
            Log.e("InputPackage", "tried accessing pointer information at invalid index: " + i);
            return null;
        }
    }

    private Vec2[] worldPoints;

    public Vec2[] points() {
        //todo chache worldpoints
//        if (worldPoints != null) {
//            Vec2[] clone = new Vec2[worldPoints.length];
//            for (int i = 0; i< worldPoints.length; i++){
//                clone[i] = worldPoints[i].clone();
//            }
//            return clone;
//        }

        if (cam == null || screen == null) {
            //Log.e("InputPackage", "CAM OR SCREEN IS NULL");
            return new Vec2[0];
        }

        Vec2[] screenPoints = screenPoints();
        for (int i = 0; i < screenPoints.length; i++) {
            screenPoints[i] = cam.screen2WorldPoint(screenPoints[i], screen);
        }

        //Log.d("input info", Arrays.toString(screenPoints));
        worldPoints = screenPoints;
        return screenPoints;
    }

    public Vec2 point(int i) {
        if (cam == null || screen == null)
            return null;

        Vec2 point = screenPoint(i);

        if (point == null)
            return null;

        return cam.screen2WorldPoint(point, screen);
    }
}
