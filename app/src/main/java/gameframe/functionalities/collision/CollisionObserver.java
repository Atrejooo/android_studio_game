package gameframe.functionalities.collision;

import gameframe.nodes.Node;

public interface CollisionObserver {
    void onCollisionEnter(Node other);

    void onCollisionStay(Node other);

    void onCollisionLeave(Node other);
}
