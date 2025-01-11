package gameframe.functionalities.syncing;

import android.util.Log;

import gameframe.conductors.Conductor;
import gameframe.nodes.Disposable;
import iddealer.Idable;
import synchronizer.SyncedEvent;
import synchronizer.SyncedEventData;

public class DisposeSyncedEvent implements SyncedEvent {
    private int typeId;
    private Idable toDispose;
    private int rememberDisposedInstanceId;
    private SyncedEventExecutor executor;
    private boolean succes = true;

    public DisposeSyncedEvent(Conductor conductor, Idable toDispose, SyncedEventExecutor executor) {
        typeId = conductor.getIdDealer().typeToTypeId(DisposeSyncedEvent.class);
        this.toDispose = toDispose;
        this.executor = executor;

        rememberDisposedInstanceId = toDispose.instanceId();

        if (toDispose instanceof Disposable) {
            ((Disposable) toDispose).dispose();
            succes = true;
        } else {
            Log.e("DisposeSyncedEvent", "attempt to dispose non-disposable Object");
        }
    }


    @Override
    public SyncedEventData getData() {
        if (succes) {
            return new DisposeSyncedEventData(typeId, rememberDisposedInstanceId, executor.instanceId());
        } else {
            return null;
        }
    }

    @Override
    public int instanceId() {
        return -1; //no instanceId needed
    }

    @Override
    public int typeId() {
        return typeId;
    }

    @Override
    public void setInstanceId(int instanceId) {
        //do nothing
    }

    @Override
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
