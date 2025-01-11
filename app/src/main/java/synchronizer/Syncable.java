package synchronizer;

import java.io.Serializable;

import iddealer.Idable;

public interface Syncable extends Idable {
    SyncableData getData();

    void giveData(SyncableData data);

    /**
     * Does this want to be synced. This takes the method call as a sign that it will be synced and it might
     * reset its syncing condition.
     * ONLY CALL IF CERTAIN!
     * @return
     */
    boolean performSync();

    //for calling a data process from outside
    //void callProcessData();
}
