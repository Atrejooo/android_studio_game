package synchronizer;

import java.io.Serializable;

import iddealer.Idable;

public interface SyncedEvent extends Idable {

    SyncedEventData getData();
   // void replicateEvent(SyncedEventData data); dont think is needed
}
