package ufogame.gamemanager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import gameframe.conductors.PlayerManager;
import synchronizer.SyncableData;

public class GameManagerSyncableData implements SyncableData, Serializable {
    Map<Integer, PlayerInfo> playerInfos;

    @Override
    public boolean containsNull() {
        return playerInfos == null;
    }

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
}
