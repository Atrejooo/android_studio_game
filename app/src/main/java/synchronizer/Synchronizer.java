package synchronizer;

import java.io.Serializable;

public interface Synchronizer {
    /**
     * Pushes all enforced events to the clients and syncs all Syncalbles that want to be synced in
     * that moment.
     * Srarts continues syncing as a client or a sync step + (continues action package reading) on the host.
     * The SyncContext should be able to provide a {@code Connector} instance with
     * a running channel.
     *
     * @throws NullPointerException
     */
    void sync() throws NullPointerException;

    /**
     * If this is not Enforcing/is a client it will stop the continues syncing thread.
     */
    void stopSyncing();

    /**
     * If this is enforcer take event data and enforce it on the client after next sync call.
     *
     * @param eventData
     */
    void enforceEvent(SyncedEventData eventData);

    /**
     * If this is a client it will send the {@code ActionPackage} to the host.
     *
     * @param actionPackage
     */
    void sendActionPackage(ActionPackage actionPackage) throws NullPointerException;

    /**
     * Set the context which is to be synced by this
     *
     * @param context
     */
    void setSyncContext(SyncContext context);

    /**
     * is this the enforcer
     */
    boolean isEnforcing();
}
