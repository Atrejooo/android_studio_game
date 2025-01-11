package gameframe.utils.quadtrees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import gameframe.utils.Vec2;
import visual.activities.R;

public class LiteQuadTree<RectType extends Rect> {
    public final int subdivisionThreshold = 2;
    public Rect maxDimensions;
    private LinkedList<Branch<RectType>> branches = new LinkedList<>();

    public LiteQuadTree(LinkedList<RectType> items) throws NullPointerException {
        if (items == null) {
            throw new NullPointerException("items list is null");
        }

        //min max search
        Vec2 min = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
        Vec2 max = new Vec2(-Float.MAX_VALUE, -Float.MAX_VALUE);

        // calculate min max points for all rects
        for (RectType rect : items) {
            Vec2 rectBottomPos = rect.pos.add(rect.size.mult(new Vec2(1, -1)));

            if (max.x < rectBottomPos.x)
                max.x = rectBottomPos.x;
            if (min.y > rectBottomPos.y)
                min.y = rectBottomPos.y;
            if (min.x > rect.pos.x)
                min.x = rect.pos.x;
            if (max.y < rect.pos.y)
                max.y = rect.pos.y;
        }

        //set dimensions to the new borders
        maxDimensions = new Rect(new Vec2(min.x, max.y), new Vec2(max.x - min.x, max.y - min.y));

        Branch firstBranch = new Branch<RectType>(maxDimensions, items);
        branches.add(firstBranch);
        firstBranch.subdivide = items.size() > subdivisionThreshold;

        for (int i = 0; i < branches.size(); i++) {
            Branch<RectType> parentBranch = branches.get(i);

            LinkedList<RectType> parentGroup = parentBranch.itemGroup;
            Rect parentDimensions = parentBranch.dimensions;

            if (parentBranch.subdivide) {
                Rect[] quarters = parentDimensions.quarters();

                Branch<RectType>[] children = new Branch[4];

                parentBranch.children = children;


                int[] containmentCounts = new int[4];

                for (int quarterIndex = 0; quarterIndex < 4; quarterIndex++) {
                    Rect quarter = quarters[quarterIndex];

                    LinkedList<RectType> group = new LinkedList<RectType>();

                    Branch<RectType> childBranch = new Branch<RectType>(quarter, group);
                    branches.add(childBranch);

                    children[quarterIndex] = childBranch;

                    for (int parentItemIndex = 0; parentItemIndex < parentGroup.size() && parentItemIndex > -1; parentItemIndex++) {
                        RectType item = parentGroup.get(parentItemIndex);
                        if (quarter.overlaps(item)) {

                            if (quarter.contains(item)) {
                                //remove item from this branch because it is fully contained in the subBranch
                                parentGroup.remove(item);

                                //go back 1 to account for the removed item
                                parentItemIndex--;

                                //count fully contained items
                                containmentCounts[quarterIndex]++;
                            }
                            //put item in new child branch
                            group.add(item);
                        }
                    }

                    childBranch.subdivide = containmentCounts[quarterIndex] > subdivisionThreshold;
                }
            }
        }
    }

    public List<List<RectType>> overlappingGroups(Rect shape) {
        List<List<RectType>> result = new LinkedList<>();

        if(branches.size() == 0) return result;
        LinkedList<Branch<RectType>> explore = new LinkedList<>();

        explore.add(branches.getFirst());

        while (explore.size() > 0){
            Branch currentBranch = explore.removeFirst();

            if(currentBranch.dimensions.overlaps(shape)){
                if(currentBranch.subdivide){
                    Branch<RectType>[] children = currentBranch.children;

                    for(Branch<RectType> child : children){
                        explore.add(child);
                    }
                }else {
                    result.add(currentBranch.itemGroup);
                }
            }
        }

        return result;
    }

    public LinkedList<Rect> getSections() {

        LinkedList<Rect> result = new LinkedList<Rect>();

        for (Branch<RectType> branch : branches) {
            if (!branch.subdivide) {
                result.add(branch.dimensions);
            }
        }

        return result;
    }

    public List<List<RectType>> itemGroups() {
        List<List<RectType>> result = new LinkedList<>();

        for (Branch<RectType> branch : branches) {
            if (!branch.subdivide) {
                // Convert branch.itemGroup to an array and add it to the LinkedList
                result.add(new ArrayList<RectType>(branch.itemGroup));
            }
        }

        return new ArrayList<List<RectType>>(result);
    }


    public class Branch<RectType extends Rect> {
        public Rect dimensions;
        public LinkedList<RectType> itemGroup;
        public boolean subdivide;
        public Branch<RectType>[] children;

        public Branch(Rect dimensions, LinkedList<RectType> itemGroup) {
            this.dimensions = dimensions;
            this.itemGroup = itemGroup;
        }
    }
}
