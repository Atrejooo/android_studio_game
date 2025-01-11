package gameframe.testUtils;

import android.util.Log;

import gameframe.functionalities.collision.OverlapCircle;
import gameframe.nodes.Comp;
import gameframe.nodes.Node;
import gameframe.utils.Vec2;

public class TestOverlap extends Comp {
    public TestOverlap(Node node){
        super(node);
    }

    @Override
    public void update(){
        Vec2[] points = conductor.input().points();
        if(points.length > 0)
        {
            Vec2 point = points[0];

            OverlapCircle overlapCircle = new OverlapCircle(1, point, conductor);

            Log.d("Test Overlap", overlapCircle.collisions().toString());
        }
    }
}
