package gameframe.functionalities.syncing;

import iddealer.Idable;
import synchronizer.SyncedEventData;

public interface SyncedEventExecutor extends Idable {
    //SyncedEventData performEvent(Idable executor);

    void replicateEvent(SyncedEventData data);
}
