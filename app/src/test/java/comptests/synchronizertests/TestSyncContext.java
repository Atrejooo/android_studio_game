package comptests.synchronizertests;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import connector.Connector;
import connector.mockconnector.MockConnector;
import gameframe.functionalities.syncing.SyncedEventExecutor;
import iddealer.IdDealer;
import iddealer.IdDealerFactory;
import iddealer.Idable;
import synchronizer.ActionPackage;
import synchronizer.SyncContext;
import synchronizer.Syncable;
import synchronizer.SyncableData;
import synchronizer.SyncedEvent;
import synchronizer.SyncedEventData;
import synchronizer.Synchronizer;
import synchronizer.SynchronizerFactory;

public class TestSyncContext implements SyncContext {
    public static final String debugName = "TestSyncContext";


    public Connector myConnector = new MockConnector();
    public IdDealer idDealer = IdDealerFactory.createIdDealer();
    public Synchronizer synchronizer = SynchronizerFactory.createSynchronizer();


    public TestSyncContext() {
        idDealer.defineTypes(new Class[]{
                TestSyncable.class
        });

        synchronizer.setSyncContext(this);
    }

    public void executeSyncTest() {
        synchronizer.sync();
    }

    public void startConnection(boolean host) {
        if (host) {
            myConnector.open(1);

            for (int i = 0; i < 20; i++) {
                addSyncable();
            }
        } else {
            myConnector.connect("", 1);
        }
    }

    public void addSyncable() {
        Syncable syncable = new TestSyncable();
        idDealer.addIdableInstance(syncable);
        syncables.add(syncable);
    }

    public List<Syncable> syncables = new ArrayList<>();

    @Override
    public void replicateSyncable(SyncableData data) {
        Class type = idDealer.fromTypeId(data.typeId());

        if (type == null) {
            Log.e("TestSyncContext", "the type: " + data.typeId() + " does not seem to exist");
            return;
        }

        try {
            if (TestSyncable.class.isAssignableFrom(type)) {
                Constructor<?> constructor = type.getConstructor();
                Object instance = constructor.newInstance();

                try {
                    Syncable newSyncable = (Syncable) instance;
                    newSyncable.giveData(data);

                    syncables.add(newSyncable);

                    idDealer.addIdableInstance(newSyncable, data.instanceId());
                } catch (Exception e) {
                    Log.e(debugName, e.getMessage() + " happened on Syncable casting");
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(debugName, "happened on Syncable recreation", e);
        } catch (InstantiationException e) {
            Log.e(debugName, "happened on Syncable recreation", e);
        } catch (InvocationTargetException e) {
            Log.e(debugName, "happened on Syncable recreation", e);
        } catch (NoSuchMethodException e) {
            Log.e(debugName, "happened on Syncable recreation", e);
        }
    }

    @Override
    public void replicateSyncedEvent(SyncedEventData syncedEventData) {
        Class eventType = idDealer.fromTypeId(syncedEventData.eventTypeId());

        if (eventType == null) {
            Log.e(debugName, "the type: " + syncedEventData.eventTypeId() + " does not seem to exist");
            return;
        }

        if (SyncedEvent.class.isAssignableFrom(eventType)) {
            Idable instance = idDealer.fromInstanceId(syncedEventData.executorId());
            if (instance != null) {
                try {
                    SyncedEventExecutor executor = (SyncedEventExecutor) instance;

                    executor.replicateEvent(syncedEventData);
                } catch (Exception e) {
                    Log.e(debugName, e.getMessage() + " the instance under the instance id " + syncedEventData.executorId() + " is not a SyncedEventExecutor");
                }
            } else {
                Log.e(debugName, "the executor of event: " + syncedEventData.eventTypeId() + " does not seem to exist");
            }
        } else {
            Log.e(debugName, "the type: " + syncedEventData.eventTypeId() + " does not seem to be an event");
        }
    }

    @Override
    public IdDealer getIdDealer() {
        return idDealer;
    }

    @Override
    public Connector getConnector() {
        return myConnector;
    }

    @Override
    public List<Syncable> getSyncables() {
        return syncables;
    }

    @Override
    public void giveActionPackage(ActionPackage actionPackages) {

    }

    public int clientCount = 0;

    @Override
    public void clientJoined(int id) {
        clientCount++;
    }

    @Override
    public void clientLeft(int id) {
        clientCount--;
    }
}
