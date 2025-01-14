package comptests.gameframetests;

import gameframe.nodes.Comp;
import gameframe.nodes.Node;

public class TestComp extends Comp {
    public TestComp(Node node) {
        super(node);
    }

    @Override
    public void awake() {
        UpdateTest.calledAwake = true;
    }

    @Override
    public void update() {
        UpdateTest.calledUpdate = true;
        UpdateTest.updateCalls++;
    }
}
