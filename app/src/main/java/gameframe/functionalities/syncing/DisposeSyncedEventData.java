package gameframe.functionalities.syncing;

import java.io.Serializable;

import synchronizer.SyncedEventData;

public class DisposeSyncedEventData implements SyncedEventData, Serializable {
    private int typeId;
    private int disposedId;
    private int executorId;

    public DisposeSyncedEventData(int eventTypeId, int disposedId, int executorId) {
        this.typeId = eventTypeId;
        this.disposedId = disposedId;
        this.executorId = executorId;
    }

    @Override
    public int executorId() {
        return executorId;
    }

    @Override
    public int[] objectIds() {
        return new int[]{disposedId};
    }

    @Override
    public Serializable[] resultingValues() {
        return new Serializable[0];
    }


    @Override
    public int eventTypeId() {
        return typeId;
    }
}
