package ufogame.gamemanager;

import gameframe.conductors.Conductible;
import gameframe.conductors.Conductor;
import gameframe.nodes.SyncableNodeWrapper;
import synchronizer.SyncableData;

public class GameManager extends SyncableNodeWrapper {

    public GameManager(Conductor conductor) {
        super(conductor);
    }

    public GameManager(Conductor conductor, int instanceId) {
        super(conductor, instanceId);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void processData(SyncableData data) {

    }

    @Override
    public SyncableData getData() {
        return null;
    }
}
