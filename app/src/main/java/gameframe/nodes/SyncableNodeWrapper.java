package gameframe.nodes;

import android.util.Log;

import gameframe.conductors.Conductor;
import gameframe.functionalities.syncing.DisposeSyncedEvent;
import gameframe.functionalities.syncing.DisposeSyncedEventData;
import gameframe.functionalities.syncing.SyncedEventExecutor;
import iddealer.Idable;
import synchronizer.Syncable;
import synchronizer.SyncableData;
import synchronizer.SyncedEventData;
import synchronizer.Synchronizer;

public abstract class SyncableNodeWrapper extends NodeWrapper implements Syncable, Idable, SyncedEventExecutor {
    public SyncableNodeWrapper(Conductor conductor) {
        super(conductor);
        conductor.getIdDealer().addIdableInstance(this);
        init();
    }

    public SyncableNodeWrapper(Conductor conductor, int instanceId) {
        super(conductor);
        conductor.getIdDealer().addIdableInstance(this, instanceId);
        init();
    }

    protected abstract void init();

    @Override
    public void setConductor(Conductor conductor) {
        super.setConductor(conductor);

        if (conductor != null) {
            conductor.addSyncable(this);
        }
    }

    @Override
    public void removeConductor() {
        if (conductor != null) {
            conductor.removeSyncable(this);
            conductor.getIdDealer().removeIdableInstance(this);
        }

        super.removeConductor();
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

    private SyncableData currentData;
    private SyncableData currentData2;
    @Override
    public final void giveData(SyncableData data) {
        if (data.containsNull()) {
            Log.e("BallWrapper", "invalid data, contains null: " + data);
            return;
        }
        currentData = data;
    }

    protected abstract void processData(SyncableData data);

    @Override
    public final boolean performSync() {
        boolean remember = performSync;
        performSync = false;
        return remember || defineSyncingCondition();
    }

    protected boolean defineSyncingCondition() {
        return false;
    }

    @Override
    public final void replicateEvent(SyncedEventData data) {
        if (data instanceof DisposeSyncedEventData) {
            Idable instance = conductor.getIdDealer().fromInstanceId(data.objectIds()[0]);

            if (instance instanceof Disposable) {
                ((Disposable) instance).dispose();
            }
        } else {
            replicateOwnEvent(data);
        }
    }

    private void replicateOwnEvent(SyncedEventData data) {

    }

    public void enforceDispose() {
        if (conductor != null) {
            conductor.synchronizer().enforceEvent(
                    new DisposeSyncedEvent(conductor, this, this)
                            .getData());
        }
    }

    /**
     * minimal delay between syncs
     */
    protected float syncDelay = 3f;
    private float lastSyncTime = Float.MAX_VALUE * -1;
    private boolean performSync = false;

    @Override
    public final void update() {
        if (!active()) {
            return;
        }
        if (currentData != null) {
            processData(currentData);
            currentData = null;
        }

        Synchronizer synchronizer = conductor.synchronizer();
        if (conductor.isSyncing()) {
            if (synchronizer.isEnforcing()) {
                if (!performSync) {
                    float currentTime = conductor.unscaledTime();
                    if (lastSyncTime + syncDelay < currentTime) {
                        performSync = true;
                        lastSyncTime = currentTime;
                    }
                }

                enforcerUpdate();
            } else {
                clientUpdate();
            }
        }
        commonUpdate();
    }

    @Override
    public final void lateUpdate() {
        if (!active()) {
            return;
        }
        Synchronizer synchronizer = conductor.synchronizer();
        if (conductor.isSyncing()) {
            if (synchronizer.isEnforcing()) {
                enforcerLateUpdate();
            } else {
                clientLateUpdate();
            }
        }
        commonLateUpdate();
    }

    /**
     * Update chain -> update = {({@code enforcerUpdate}|{@code clientUpdate})->{@code commonUpdate}} lateUpdate = {({@code enforcerLateUpdate}|{@code clientLateUpdate})->{@code commonLateUpdate}}
     */
    protected void enforcerUpdate() {

    }

    /**
     * Update chain -> update = {({@code enforcerUpdate}|{@code clientUpdate})->{@code commonUpdate}} lateUpdate = {({@code enforcerLateUpdate}|{@code clientLateUpdate})->{@code commonLateUpdate}}
     */
    protected void enforcerLateUpdate() {

    }

    /**
     * Update chain -> update = {({@code enforcerUpdate}|{@code clientUpdate})->{@code commonUpdate}} lateUpdate = {({@code enforcerLateUpdate}|{@code clientLateUpdate})->{@code commonLateUpdate}}
     */
    protected void clientUpdate() {

    }

    /**
     * Update chain -> update = {({@code enforcerUpdate}|{@code clientUpdate})->{@code commonUpdate}} lateUpdate = {({@code enforcerLateUpdate}|{@code clientLateUpdate})->{@code commonLateUpdate}}
     */
    protected void clientLateUpdate() {

    }

    /**
     * Update chain -> update = {({@code enforcerUpdate}|{@code clientUpdate})->{@code commonUpdate}} lateUpdate = {({@code enforcerLateUpdate}|{@code clientLateUpdate})->{@code commonLateUpdate}}
     */
    protected void commonUpdate() {

    }

    /**
     * Update chain -> update = {({@code enforcerUpdate}|{@code clientUpdate})->{@code commonUpdate}} lateUpdate = {({@code enforcerLateUpdate}|{@code clientLateUpdate})->{@code commonLateUpdate}}
     */
    protected void commonLateUpdate() {

    }
}
