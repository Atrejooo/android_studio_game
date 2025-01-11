package common.data;

import gameframe.nodes.Transform;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.utils.*;


public abstract class RendererData extends Transform {
    //layering
    public int layer = 0;
    public LayerGroup layerGroup = LayerGroup.DEFAULT;
    public int zOffset = 0;
    private Color color = new Color(1f, 1f, 1f, 1f);

    public RendererData(Transform transform) throws IllegalArgumentException {
        if (transform == null)
            throw new IllegalArgumentException("transform cannot be null");

        copyValues(transform);
    }

    private float pxPerUnit = 55f;

    public void setPxPerUnit(float pxPerUnit) {
        if (pxPerUnit > 0) {
            this.pxPerUnit = pxPerUnit;
        }
    }

    public float pxPerUnit() {
        return pxPerUnit;
    }

    public int getLayer() {
        return layer + zOffset;
    }

    public Color color() {
        return color;
    }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }

    private Vec2 center = new Vec2(0.5f, 0.5f);

    public void setCenter(Vec2 center) {
        if (center == null) return;
        this.center = center;
    }

    public Vec2 center() {
        return center.clone();
    }
}
