package gameframe.utils.quadtrees;

import gameframe.functionalities.collision.ICollider;
import gameframe.utils.Vec2;

public class ColliderRect extends Rect {
    public ColliderRect(Vec2 pos, Vec2 size, ICollider collider) {
        super(pos, size);
        this.collider = collider;
    }

    public ICollider collider;
}
