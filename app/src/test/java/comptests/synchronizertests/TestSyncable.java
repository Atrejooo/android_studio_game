package comptests.synchronizertests;

import singeltons.Randoms;
import synchronizer.Syncable;
import synchronizer.SyncableData;

public class TestSyncable implements Syncable {
    public int value1 = Randoms.randomInt();
    public float value2 = Randoms.range01();


    @Override
    public SyncableData getData() {
        TestSyncableData syncableData = new TestSyncableData();

        syncableData.setInstanceId(instanceId);
        syncableData.setTypeId(typeId);

        syncableData.value1 = value1;
        syncableData.value2 = value2;

        return syncableData;
    }

    @Override
    public void giveData(SyncableData data) {
        TestSyncableData testSyncableData = (TestSyncableData) data;

        value1 = testSyncableData.value1;
        value2 = testSyncableData.value2;
    }

    @Override
    public boolean performSync() {
        return true;
    }

    int instanceId;

    @Override
    public int instanceId() {
        return instanceId;
    }

    int typeId;

    @Override
    public int typeId() {
        return typeId;
    }

    @Override
    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
