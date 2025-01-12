package ufogame.ufoplayer;

import java.io.Serializable;

import gameframe.utils.Vec2;
import synchronizer.SyncedEventData;

public class UfoShockWaveSyncedData implements SyncedEventData, Serializable {
    int executorId;
    int eventTypeId;
    int[] objectIds;
    Vec2[] newVelos;

    @Override
    public int eventTypeId() {
        return eventTypeId;
    }

    @Override
    public int executorId() {
        return executorId;
    }

    @Override
    public int[] objectIds() {
        return objectIds;
    }

    @Override
    public Serializable[] resultingValues() {
        return newVelos;
    }
}
