package synchronizer;

import java.io.Serializable;

import iddealer.Idable;

/**
 * It's type Id represents the type id of the SyncedEvent not the data.
 */
public interface SyncedEventData {
    int eventTypeId();

    int executorId();

    int[] objectIds();

    Serializable[] resultingValues();
}
