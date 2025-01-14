package comptests.integrationtest;

import gameframe.conductors.Conductor;
import gameframe.functionalities.movement.TestVelocityMovement;
import gameframe.nodes.Node;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.scenes.Scene;
import gameframe.utils.Vec2;
import singeltons.Randoms;
import synchronizer.SyncableData;

public class TestSyncableNodeWrapper extends SyncableNodeWrapper {
    public TestSyncableNodeWrapper(Conductor conductor, int instanceId) {
        super(conductor, instanceId);
    }

    public TestSyncableNodeWrapper(Conductor conductor) {
        super(conductor);
    }

    Node node;
    TestVelocityMovement testVelocityMovement;

    @Override
    protected void init() {
        Scene scene = conductor.activeScene();
        node = scene.add(new Vec2(Randoms.range(-100, 100), Randoms.range(-100, 100)), Integer.toString(instanceId()));

        testVelocityMovement = node.add(TestVelocityMovement.class);
        testVelocityMovement.setaLinDrag(0.1f);
        testVelocityMovement.setLinDrag(20);
        testVelocityMovement.setBounceF(0.5f);

        syncDelay =0;
    }

    @Override
    protected void processData(SyncableData data) {
        TestSyncableData testSyncableData = (TestSyncableData) data;

        testVelocityMovement.setVelo(testSyncableData.velo);
        node.transform().setPos(testSyncableData.pos);
    }

    @Override
    public SyncableData getData() {
        TestSyncableData data = new TestSyncableData();
        data.setInstanceId(instanceId());
        data.setTypeId(typeId());

        data.velo = testVelocityMovement.velo();
        data.pos = node.transform().pos();

        return data;
    }




}
