package gameframe.functionalities.collision;

import java.util.List;

import gameframe.nodes.Node;
import gameframe.utils.Vec2;
import gameframe.utils.quadtrees.ColliderRect;

public interface ICollider {

    int thisIs();

    int theyAre();

    void setThisIs(int value);

    void setTheyAre(int value);

    /**
     * sets a single bit in the layer defining what this is
     * @param index of the bit
     * @param value of the bit
     */
    default void setThisBit(int index, boolean value) {
        if (value) {
            //set the bit to 1
            setThisIs(thisIs() | (0b1 << index));
        } else {
            //set the bit to 0
            setThisIs(thisIs() & (~(0b1 << index)));
        }
    }

    /**
     * sets a single bit in the layer defining what other collision shapes are which this can interact with
     * @param index of the bit
     * @param value of the bit
     */
    default void setTheyBit(int index, boolean value) {
        if (value) {
            //set the bit to 1
            setTheyAre(theyAre() | (0b1 << index));
        } else {
            //set the bit to 0
            setTheyAre(theyAre() & (~(0b1 << index)));
        }
    }

    /**
     * adds a new collision if the partner is valid
     * @param partner as other collision shape
     */
    void addCollision(ICollider partner);

    /**
     * removes all found collisions from the collisions list until the next collision update
     */
    void clearCollisions();

    /**
     * returns every node (physical object) that this is overlapping with on matching layers
     * @return list of Nodes
     */
    List<Node> collisions();

    /**
     * is this a circle or not (a rect)
     * @return boolean
     */
    boolean isCircle();

    /**
     * returns the current center of the collider in worldspace
     * @return pos as Vec2
     */
    Vec2 currentCenterPos();

    /**
     * returns a rectangle representing the bounds of the collider
     */
    ColliderRect rect();
}
