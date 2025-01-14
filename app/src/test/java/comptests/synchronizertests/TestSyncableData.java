package comptests.synchronizertests;

import java.io.Serializable;

import synchronizer.SyncableData;

public class TestSyncableData implements SyncableData, Serializable {


    public int value1;
    public float value2;

    @Override
    public boolean containsNull() {
        return false;
    }

    private int instance;

    @Override
    public int instanceId() {
        return instance;
    }

    private int typeId;

    @Override
    public int typeId() {
        return typeId;
    }

    @Override
    public void setInstanceId(int instanceId) {
        this.instance = instanceId;
    }

    @Override
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
