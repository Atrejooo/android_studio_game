package gameframe.conductors;

import java.util.LinkedList;
import java.util.List;

import gameframe.functionalities.collision.Collider;
import gameframe.functionalities.collision.ICollider;
import gameframe.functionalities.prefabs.TranslucentRect;
import gameframe.nodes.Node;
import gameframe.utils.Color;
import gameframe.utils.Vec2;
import gameframe.utils.quadtrees.ColliderRect;
import gameframe.utils.quadtrees.LiteQuadTree;
import gameframe.utils.quadtrees.Rect;
import singeltons.PerformanceTester;

import android.util.Log;

public class CollisionManager extends Conductible implements DoneUpdateable {
    private LinkedList<Collider> colliders = new LinkedList<>();

    public void add(Collider collider) {
        if (collider == null)
            return;
        if (!colliders.contains(collider))
            colliders.add(collider);
    }

    public boolean remove(Collider collider) {
        return colliders.remove(collider);
    }

    private LiteQuadTree<ColliderRect> tree;

    private LinkedList<Node> debugNodes = new LinkedList<Node>();

    @Override
    public void doneUpdating() {
        LinkedList<ColliderRect> colliderRects = new LinkedList<>();

        for (Collider collider : colliders) {
            if (collider.active()) {
                colliderRects.add(collider.rect());
            }
            collider.clearCollisions();
        }

        //performance tester info
        PerformanceTester.passColliderCounts(colliders.size());

        //collision calculation and quadtree
        tree = new LiteQuadTree<ColliderRect>(colliderRects);

        List<List<ColliderRect>> groups = tree.itemGroups();

        calculateCollisions(groups);


        //test O(n)=n*n
//        List<List<ColliderRect>> groups = new LinkedList<>();
//        groups.add(colliderRects);
//        calculateCollisions(groups);

        boolean debug = false;
        if (!debug) return;
        //debug contains
        debugContains(groups, colliderRects);

        //cam
        Vec2 camSize = tree.maxDimensions.size;
        if (camSize.y * 2 > camSize.x)
            conductor.camera().setSizeX(camSize.y * 2.4f);
        else
            conductor.camera().setSizeX(camSize.x * 1.2f);

        //debugging branches
        LinkedList<Rect> branchDimensions = tree.getSections();
        for (Node node : debugNodes) {
            node.dispose();
        }
        debugNodes.clear();
        debugTree(branchDimensions);
    }

    private void calculateCollisions(List<List<ColliderRect>> groups) {
        LinkedList<CollisionPair> pairs = new LinkedList<>();
        for (List<ColliderRect> group : groups) {
            int groupSize = group.size();

            for (int colliderIndex = 0; colliderIndex < groupSize - 1; colliderIndex++) {
                ColliderRect colliderRect1 = group.get(colliderIndex);

                for (int i = colliderIndex + 1; i < groupSize; i++) {
                    ColliderRect colliderRect2 = group.get(i);

                    addPair(pairs, colliderRect1, colliderRect2);
                }
            }
        }
        CollisionPair[] pairArray = pairs.stream()
                .distinct()
                .toArray(CollisionPair[]::new);

        PerformanceTester.passPairCount(pairArray.length);
        for (CollisionPair pair : pairArray) {
            if (compareColliders(pair.collider1, pair.colliderRect1, pair.collider2, pair.colliderRect2)) {
                if (pair.collider1WithOther) {
                    pair.collider1.addCollision(pair.collider2);
                }
                if (pair.collider2WithOther) {
                    pair.collider2.addCollision(pair.collider1);
                }
            }
        }
    }

    public void overlapShape(ICollider shape) {
        if(tree == null)
            return;

        ColliderRect shapeRect = shape.rect();

        //TODO
        List<List<ColliderRect>> groups = tree.overlappingGroups(shapeRect);

        // for group in groups : pair with shape with addPair
        LinkedList<ICollider> rememberColliders = new LinkedList<>();

        for (List<ColliderRect> group : groups) {
            for (ColliderRect colliderRect : group) {
                ICollider collider = colliderRect.collider;

                boolean collisionMaskMatch = (shape.theyAre() & collider.thisIs()) != 0;
                if (collisionMaskMatch) {
                    if (rememberColliders.contains(collider)) {
                        continue;
                    }
                    rememberColliders.add(collider);

                    if (compareColliders(shape, shapeRect, collider, colliderRect)) {
                        shape.addCollision(collider);
                    }
                }
            }
        }
    }

    private void addPair(List<CollisionPair> pairs, ColliderRect colliderRect1, ColliderRect colliderRect2) {
        ICollider collider1 = colliderRect1.collider;
        ICollider collider2 = colliderRect2.collider;

        boolean collider1WithOther = (collider1.theyAre() & collider2.thisIs()) != 0;
        boolean collider2WithOther = (collider1.thisIs() & collider2.theyAre()) != 0;

        if (collider1WithOther || collider2WithOther)
            pairs.add(new CollisionPair(colliderRect1, colliderRect2, collider1WithOther, collider2WithOther));
    }

    public boolean compareColliders(ICollider collider1, ICollider collider2) {
        return compareColliders(collider1, collider1.rect(), collider2, collider2.rect());
    }

    public boolean compareColliders(ICollider collider1, Rect rect1, ICollider collider2, Rect rect2) {
        if (collider1.isCircle()) {
            //collider 1 is a circle
            if (collider2.isCircle()) {
                //collider 2 is a circle as well
                return circleXCircle(collider1.currentCenterPos(), rect1.size.x / 2, collider2.currentCenterPos(), rect2.size.x / 2);
            } else {
                //collider 2 is a rect
                return circleXRect(collider1.currentCenterPos(), rect1.size.x / 2, rect2);
            }
        } else {
            //collider 1 is a rect
            if (collider2.isCircle()) {
                //collider 2 is a circle instead
                return circleXRect(collider2.currentCenterPos(), rect2.size.x / 2, rect1);
            } else {
                //collider 2 is a rect as well
                return rectXRect(rect1, rect2);
            }
        }
    }

    public boolean rectXRect(Rect rect1, Rect rect2) {
        return rect1.overlaps(rect2);
    }

    public boolean circleXCircle(Vec2 pos1, float r1, Vec2 pos2, float r2) {
        return pos1.distance(pos2) < r1 + r2;
    }

    public boolean circleXRect(Vec2 posCircle, float radius, Rect rect) {
        return rect.overlapsCircle(posCircle, radius);
    }


    private <RectType extends Rect> void debugTree(LinkedList<RectType> rects) {
        if (conductor == null)
            return;


        for (int i = 0; i < rects.size(); i++) {
            RectType rect = rects.get(i);

            float factor = 0.04f;
            Vec2 smallSize = rect.size.mult(1f - factor);
            Rect smallRect = new Rect(rect.pos.add(new Vec2(rect.size.x * factor * 0.5f, -rect.size.y * factor * 0.5f)), smallSize);

            Color color = new Color(0, 0, 1, 0.2f);

            debugNodes.add(TranslucentRect.create(smallRect, color, conductor));
        }
    }

    private <RectType extends Rect> void debugContains(List<List<RectType>> rectGroups, LinkedList<ColliderRect> items) {
        int containmentCount = 0;

        for (ColliderRect item : items) {
            for (List<RectType> list : rectGroups) {
                if (list.contains(item)) {
                    containmentCount++;
                    break;
                }
            }
        }

        Log.d("collision manager", "contained Items in the tree: " + containmentCount);
    }

    private class CollisionPair {
        public ICollider collider1, collider2;
        public ColliderRect colliderRect1, colliderRect2;

        public boolean collider1WithOther, collider2WithOther;

        public CollisionPair(ColliderRect colliderRect1, ColliderRect colliderRect2, boolean collider1WithOther, boolean collider2WithOther) {
            this.colliderRect1 = colliderRect1;
            this.colliderRect2 = colliderRect2;
            this.collider1 = colliderRect1.collider;
            this.collider2 = colliderRect2.collider;
            this.collider1WithOther = collider1WithOther;
            this.collider2WithOther = collider2WithOther;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof CollisionPair)) {
                return false;
            }

            CollisionPair other = (CollisionPair) o;

            if (collider1 != other.collider1 && collider1 != other.collider2)
                return false;

            if (collider2 != other.collider2 && collider2 != other.collider1)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            // Use the unordered combination of collider1 and collider2
            int hash1 = (collider1 == null) ? 0 : collider1.hashCode();
            int hash2 = (collider2 == null) ? 0 : collider2.hashCode();
            return hash1 ^ hash2; // XOR to ensure order doesn't matter
        }
    }
}
