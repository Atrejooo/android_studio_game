package gameframe.functionalities.hitbox;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;

public class HitBox extends Comp {
    public HitBox(Node node) {
        super(node);
    }

    private HitBoxObserver observer;

    public void setObserver(HitBoxObserver observer) {
        this.observer = observer;
    }

    public void hit() {
        observer.onHit();
    }
}
