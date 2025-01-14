package comptests.integrationtest;

import java.util.List;

import gameframe.conductors.Conductor;
import gameframe.nodes.Node;
import gameframe.scenes.Scene;
import gameframe.utils.ConnectionSource;

public class TestScene extends Scene {
    boolean open;

    public TestScene(boolean open) {
        this.open = open;
    }

    @Override
    public void init(Conductor conductor) {
        super.init(conductor);

        for (int i = 0; i < 20; i++) {
            new TestSyncableNodeWrapper(conductor);
        }
        ConnectionSource source = new ConnectionSource();
        source.open = open;
        source.port = 1;
        source.ip = "";
        conductor.startConnector(source, (result -> {
            if (result)
                conductor.startSyncing(null);
        }));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public Class[] getIdableTypes() {
        return new Class[]{
                TestSyncableNodeWrapper.class
        };
    }
}
