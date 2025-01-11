package ufogame.ufoplayer;

import java.io.Serializable;

import gameframe.utils.Vec2;
import synchronizer.SyncableData;

public class UfoPlayerSyncableData implements SyncableData, Serializable {
    Vec2 pos;
    Vec2 vel;

    Vec2 inputDir = new Vec2();

    int playerId;
    private int instanceId;
    private int typeId;

    @Override
    public int instanceId() {
        return instanceId;
    }

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

    @Override
    public boolean containsNull() {
        return pos == null || inputDir == null || vel == null;
    }
}
