package comptests.integrationtest;

import java.io.Serializable;

import gameframe.utils.Vec2;
import synchronizer.SyncableData;

public class TestSyncableData implements SyncableData, Serializable {

    Vec2 pos;
    Vec2 velo;

    @Override
    public boolean containsNull() {
        return pos != null && velo != null;
    }

    @Override
    public int instanceId() {
        return 0;
    }

    @Override
    public int typeId() {
        return 0;
    }

    @Override
    public void setInstanceId(int instanceId) {

    }

    @Override
    public void setTypeId(int typeId) {

    }
}
