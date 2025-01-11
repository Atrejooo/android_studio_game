package synchronizer;

import java.util.List;

import connector.Connector;
import iddealer.IdDealer;

public interface SyncContext {

    /**
     * Called when the context has to replecate an Object that was not created yet.
     *
     * @param data
     */
    void replicateSyncable(SyncableData data);

    /**
     * Context should replicate an Event that happened on the enforcers side.
     */
    void replicateSyncedEvent(SyncedEventData syncedEventData);

    /**
     * This should return the IdDealer of the context.
     *
     * @return
     */
    IdDealer getIdDealer();

    /**
     * should return a Connector object to connect this game instance to other instances
     */
    Connector getConnector();

    /**
     * Returns all Syncables in the context
     */
    List<Syncable> getSyncables();

    /**
     * Gives the SyncContext the Action data of a client for handeling.
     */
    void giveActionPackage(ActionPackage actionPackages);

    /**
     * Player joined
     */
    void clientJoined(int id);

    /**
     * Player left
     */
    void clientLeft(int id);
}
