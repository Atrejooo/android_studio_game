package gameframe.functionalities.rendering;

import common.ICamera;
import gameframe.conductors.Conductor;
import gameframe.functionalities.movement.VelocityMovement;
import gameframe.nodes.*;
import gameframe.utils.*;

public class Camera extends Comp implements ICamera {
    public Camera(Node node) {
        super(node);
    }

    @Override
    public void removeConductor() {
        if (conductor.camera().equals(this))
            conductor.setCamera(null);
        super.removeConductor();
    }

    @Override
    public void setConductor(Conductor conductor) {
        super.setConductor(conductor);
        conductor.setCamera(this);
    }

    private Vec2 worldPos = new Vec2(0, 0);

    private float sizeX = 10;
    private float lastAspect = 1f;


    // flower :)
    private float petals = 5;
    private float flowerSpeed = 0.1f;

    // shake :(
    private float shake = 0;
    private float shakeDrag = 0.06f;

    // velocity based movement
    private VelocityMovement movement;

    // follow
    public Node followObj;
    private float minDistance = 1f;
    private float distanceForce = 17f;

    @Override
    public void update() {
        if (!active())
            return;
        // test movement
        //node().transform().setPos(Vec2.fromAngle(conductor.time()).mult(10f).add(new Vec2(0, 5f)));
        //sizeX = (float) Math.sin(conductor.time() * 5f) * 13f + 25;

        // follow ---------------------------------------------
        if (followObj != null && movement != null) {
            Vec2 followPos = followObj.transform().pos();
            Vec2 dir = followPos.sub(transform().pos());

            float distance = dir.mag();

            if (distance > minDistance)
                movement.addForce(dir.normal().mult(distanceForce * (distance + 0.6f) * conductor.delta()));
        }

        // screen shake and world position
        if (shake != 0) {

            float t = flowerSpeed * conductor.time();
            float flowerT = (float) Math.cos(t * petals);

            Vec2 flower = new Vec2(shake * (float) Math.cos(t) * flowerT, shake * (float) Math.sin(t) * flowerT);

            worldPos = transform().pos().add(flower);

            shake = AddF.clamp(shake - shakeDrag * conductor.delta() * 50f, 0,
                    shake);
        } else {
            worldPos = transform().pos();
        }
    }

    @Override
    public void awake() {
        movement = (VelocityMovement) node().add(VelocityMovement.class);

        if (movement != null) {
            movement.setaLinDrag(1.3f);
            movement.setLinDrag(17f);
        }
    }


    @Override
    public Vec2 world2ScreenPoint(Vec2 point, Vec2 screen) {
        float aspect = (screen.y / screen.x);
        lastAspect = aspect;

        float halveSizeX = sizeX * 0.5f;

        Vec2 screenCornerPos = worldPos
                .add(new Vec2(-halveSizeX, halveSizeX * aspect));

        Vec2 screenPoint = point.sub(screenCornerPos).mult(screen.x / sizeX);
        screenPoint.y *= -1;

        return screenPoint;
    }

    @Override
    public Vec2 screen2WorldPoint(Vec2 screenPos, Vec2 screen) {
        float aspect = (screen.y / screen.x);
        lastAspect = aspect;

        float halveSizeX = sizeX * 0.5f;
        float halveSizeY = halveSizeX * aspect;
        Vec2 screenCornerPos = worldPos
                .add(new Vec2(-halveSizeX, halveSizeY));

        Vec2 point = screenPos.mult(new Vec2(sizeX / screen.x, -halveSizeY * 2 / screen.y));

        return screenCornerPos.add(point);
    }

    @Override
    public Vec2 map(Vec2 point, float aspect) {
        lastAspect = aspect;

        Vec2 mapped = point.sub(worldPos);

        mapped.x /= sizeX / 2f;
        mapped.y /= sizeX / 2f * aspect;

        return mapped;
    }

    public Vec2 UV2World(Vec2 uv) {
        float halveSizeX = sizeX * 0.5f;

        Vec2 halveScreen = new Vec2(halveSizeX, -halveSizeX * lastAspect);

        Vec2 screenCornerPos = worldPos.sub(halveScreen);

        return screenCornerPos.add(uv.mult(halveScreen.mult(2)));
    }

    public Vec2 world2UV(Vec2 point) {
        float halveSizeX = sizeX * 0.5f;

        Vec2 halveScreen = new Vec2(halveSizeX, -halveSizeX * lastAspect);

        Vec2 screenCornerPos = worldPos.sub(halveScreen);

        return point.sub(screenCornerPos).div(halveScreen.mult(2f));
    }

    public float sizeX() {
        return sizeX;
    }

    public void setSizeX(float sizeX) {
        if (sizeX > 0.01f) {
            this.sizeX = sizeX;
        }
    }

    /**
     * returns the last aspect this camera was used for
     *
     * @return aspect ratio
     */
    public float lastAspect() {
        return lastAspect;
    }

    public void screenShake(float strenght) {
        shake += AddF.clamp(strenght - shake, 0, strenght);
    }
}
