package synchronizer;


import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import connector.Channel;
import connector.Connection;
import connector.Connector;
import connector.ConnectorFactory;
import connector.ConnectorObserver;
import iddealer.IdDealer;
import iddealer.Idable;

class SynchronizerImpl implements Synchronizer, ConnectorObserver {
    private static final String debugName = "Synchronizer";
    private static final boolean printSendLog = true;


    private IdDealer idDealer;
    private SyncContext syncContext;

    private LinkedList<SyncedEventData> eventCache = new LinkedList<>();

    private Connector connector = ConnectorFactory.createConnector();


    @Override
    public void sync() throws NullPointerException {
        connector = syncContext.getConnector();
        Objects.requireNonNull(connector, "connector is null in SyncContext");
        Channel channel = connector.getChannel();
        Objects.requireNonNull(channel, "channel is null in Connector");
        if (!channel.isOpen())
            throw new NullPointerException("channel is closed");


        isEnforcing = connector.isHost();

        if (isEnforcing)
            syncAsEnforcer(channel);
        else
            syncAsClient(channel);
    }

    private boolean stop = false;

    @Override
    public void stopSyncing() {
        stop = true;
        connector = null;
    }

    private boolean startedActionPackageThread = false;

    private void syncAsEnforcer(Channel channel) {
        if (!startedActionPackageThread) {
            stop = false;
            startedActionPackageThread = true;

            new Thread(() -> {
                while (channel.isOpen() && !stop) {
                    Object read = channel.read();

                    if (read instanceof ActionPackage) {
                        ActionPackage actionPackage = (ActionPackage) read;

                        //TODO gets called!
                        syncContext.giveActionPackage(actionPackage);
                    } else {
                        Log.e(debugName, "The Object send by a client is not an Actionpackage!");
                    }
                }
                startedActionPackageThread = false;
            }).start();
        }


        while (eventCache.size() > 0) {
            SyncedEventData data = eventCache.removeFirst();

            channel.send(data);

            if (printSendLog)
                Log.i(debugName, "host send event: " + data.toString());
        }

        List<Syncable> syncables = syncContext.getSyncables();

        for (int i = 0; i < syncables.size(); i++) {
            Syncable syncable = syncables.get(i);
            if (syncable.performSync()) {
                SyncableData data = syncable.getData();

                channel.send(data);

                if (printSendLog)
                    Log.i(debugName, "host send data: " + data.toString());
            }
        }
    }

    private boolean startedClientSyncingThread = false;

    private void syncAsClient(Channel channel) {
        idDealer = syncContext.getIdDealer();
        stop = false;

        //for calling each process data call at one spot in time
//        for(Syncable syncable : syncContext.getSyncables()){
//            syncable.callProcessData();
//        }

        if (startedClientSyncingThread)
            return;

        new Thread(() -> {
            startedClientSyncingThread = true;
            while (channel.isOpen() && !stop) {
                Object readObject = channel.read();

                if (readObject instanceof SyncableData) {
                    SyncableData syncableData = (SyncableData) readObject;

                    handleSyncableData(syncableData);
                } else if (readObject instanceof SyncedEventData) {
                    SyncedEventData syncedEventData = (SyncedEventData) readObject;

                    syncContext.replicateSyncedEvent(syncedEventData);
                }
            }

            startedClientSyncingThread = false;
        }).start();
    }

    @Override
    public void sendActionPackage(ActionPackage actionPackage) throws NullPointerException {
        connector = syncContext.getConnector();
        Objects.requireNonNull(connector, "connector is null in SyncContext on sendActionPackage attempt");
        Channel channel = connector.getChannel();
        Objects.requireNonNull(channel, "channel is null in Connector on sendActionPackage attempt");
        if (!channel.isOpen())
            throw new NullPointerException("channel is closed on sendActionPackage attempt");

        isEnforcing = connector.isHost();

        if (!isEnforcing) {
            channel.send(actionPackage);
        }
    }

    private void handleSyncableData(SyncableData data) {
        int instanceId = data.instanceId();

        Idable idable = idDealer.fromInstanceId(instanceId);
        if (idable != null) {
            if (idable instanceof Syncable) {
                ((Syncable) idable).giveData(data);
            }
        } else {
            syncContext.replicateSyncable(data);
        }
    }

    @Override
    public void enforceEvent(SyncedEventData eventData) {
        if (eventData != null)
            eventCache.add(eventData);
        else
            Log.e("Synchronizer", "event data for enforce attempt was null!");
    }

    @Override
    public void setSyncContext(SyncContext context) {
        this.syncContext = context;

        connector = syncContext.getConnector();

        if (connector != null)
            connector.setObserver(this);
        else
            throw new NullPointerException("the connector in sync context is null");
    }

    private boolean isEnforcing;

    @Override
    public boolean isEnforcing() {
        return isEnforcing;
    }

    private List<Integer> clientIds = new ArrayList<>();

    @Override
    public void onConnection(Connection newConnection) {
        clientIds.add(newConnection.clientId());

        syncContext.clientJoined(newConnection.clientId());

        for (Syncable syncable : syncContext.getSyncables()) {

            SyncableData data = syncable.getData();

            newConnection.send(data);
        }
    }

    @Override
    public void onDisconnection(Connection lostConnection) {
        syncContext.clientLeft(lostConnection.clientId());
    }
}
