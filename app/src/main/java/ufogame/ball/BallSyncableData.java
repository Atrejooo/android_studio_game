package ufogame.ball;

import gameframe.utils.Vec2;
import synchronizer.SyncableData;

public class BallSyncableData implements SyncableData {
    Vec2 pos;
    Vec2 vel;

    Vec2 accelDir;
    float maxSpeed;
    float speedF;

    int playerTargetInstanceId;

    private int updateIndex;
    private int instanceId;

    @Override
    public int instanceId() {
        return instanceId;
    }

    private int typeId;

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
        return pos == null || vel == null || accelDir == null;
    }

    @Override
    public String toString(){
        if(containsNull()){
            return pos + ", "+ vel + ", " + accelDir;
        }else {
            return super.toString();
        }
    }
}
