package gameframe.conductors;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import common.data.RendererData;
import connector.Connector;
import connector.ConnectorFactory;
import gameframe.Game;
import gameframe.functionalities.rendering.Camera;
import gameframe.functionalities.rendering.Renderer;
import gameframe.functionalities.syncing.PlayerFactory;
import gameframe.functionalities.syncing.SyncedEventExecutor;
import gameframe.looping.Loop;
import gameframe.looping.Loopable;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.scenes.Scene;
import common.data.InputPackage;
import gameframe.utils.ConnectionSource;
import gameframe.utils.Vec2;
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
import ufogame.ufoplayer.UfoActionPackage;

/**
 * The Conductor holds the manager group that the games objects need.
 */
public class Conductor extends Time implements SyncContext, DoneUpdateable {

    private UpdateManager updateManager;
    private Loop gameLoop;
    private Scene activeScene;
    private InputPackage input = new InputPackage(new Vec2[0], null, null);
    private CollisionManager collisionManager;

    //syncing
    private List<Syncable> syncables = new ArrayList<Syncable>();
    private IdDealer idDealer = IdDealerFactory.createIdDealer();
    private Synchronizer synchronizer = SynchronizerFactory.createSynchronizer();
    private Connector connector = ConnectorFactory.createConnector();
    private PlayerManager playerManager;

    //Yelling
    private List<YellListener> yellListeners = new ArrayList<>();

    //view elements
    private ArrayList<Renderer> renderers = new ArrayList<Renderer>();
    private Camera cam;

    private ConnectionSource connectionSource;

    private Scene[] scenes;

    /**
     * Sets up the manager group of the game.
     */
    public Conductor(Game game, Scene[] scenes) {
        updateManager = new UpdateManager();
        gameLoop = new Loop(new Loopable[]{this, updateManager});

        collisionManager = new CollisionManager();
        collisionManager.setConductor(this);

        updateManager.setDoneUpdateables(new DoneUpdateable[]{collisionManager, game, this});

        this.scenes = scenes;
    }

    public void yell(Yell massage) {
        for (int i = 0; i < yellListeners.size(); i++) {
            yellListeners.get(i).onYell(massage);
        }
    }

    public void addYellListener(YellListener yellListener) {
        if (!yellListeners.contains(yellListener))
            yellListeners.add(yellListener);
    }

    public void removeYellListender(YellListener yellListener) {
        yellListeners.remove(yellListener);
    }

    //control--------------------------------------

    /**
     * Starts the game loop.
     */
    public void start() {
        // start the 1st scene
        if (scenes.length > 0) {
            activeScene = scenes[0];
            activeScene.init(this);
        }

        gameLoop.start();
    }

    public void stop() {
        gameLoop.stop();
    }


    public void setFps(int fps) {
        if (fps > 0)
            gameLoop.setFps(fps);
        else
            gameLoop.setFps(30);
    }

    //updateManager--------------------------------------

    /**
     * @return the current {@code UpdateManager}
     */
    public UpdateManager updateManager() {
        return updateManager;
    }

    //collisionManager------------------------------------
    public CollisionManager collisionManager() {
        return collisionManager;
    }

    //activeScene--------------------------------------
    public Scene activeScene() {
        return activeScene;
    }

    private int switchSceneIndex = -1;

    public void switchScene(int index) {
        switchSceneIndex = index;
    }

    private void performSceneSwitch() {
        if (switchSceneIndex != -1) {
            if (startedSyncing) {
                synchronizer.stopSyncing();
                connector.stop();
                startedSyncing = false;
            }

            activeScene.dispose();
            activeScene = scenes[switchSceneIndex];
            idDealer.defineTypes(activeScene.getIdableTypes());
            activeScene.init(this);

            switchSceneIndex = -1;
        }
    }

    //Renderer--------------------------------------
    public void addRenderer(Renderer renderer) {
        if (!renderers.contains(renderer)) renderers.add(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        renderers.remove(renderer);
    }

    public RendererData[] getRendererDatas() {
        ArrayList<Renderer> activeRenderers = new ArrayList<Renderer>();

        for (Renderer renderer : renderers) {
            if (renderer.active()) {
                activeRenderers.add(renderer);
            }
        }
        //Log.d("renderer count", "count: " + activeRenderers.size());
        int size = activeRenderers.size();
        RendererData[] result = new RendererData[size];
        for (int i = 0; i < size && i < activeRenderers.size(); i++) {
            result[i] = activeRenderers.get(i).data();
        }
        return result;
    }

    //input-----------------------------------------

    /**
     * @return the current {@code InputPackage}
     */
    public InputPackage input() {
        return input;
    }

    public void setInput(InputPackage input) {
        if (input != null) {
            this.input = input;
        }
    }

    //Camera--------------------------------------
    public void setCamera(Camera cam) {
        this.cam = cam;
    }


    public Camera camera() {
        return cam;
    }

    //syncing-----------------------------------------------------------

    public void setConnectionSource(ConnectionSource connectionSource) {
        Objects.requireNonNull(connectionSource, "connection source is null");

        this.connectionSource = connectionSource;
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public PlayerManager playerManager() {
        return playerManager;
    }

    /**
     * Starts syncing the current scene/registered {@code Syncables}.
     *
     * @param source
     */
    private int myPlayerId = 0;

    public int getMyPlayerId() {
        return myPlayerId;
    }

    private boolean startedSyncing = false;

    public boolean isSyncing() {
        return startedSyncing;
    }

    private PlayerFactory playerFactory;

    public void startConnector(ConnectionSource source, ConnectionAttemptObserver observer) {
        new Thread(() -> {
            if (source.open)
                connector.open(source.port);
            else
                connector.connect(source.ip, source.port);

            if (connector.getChannel() == null) {
                Log.e("Conductor", "The channel creation or connection did not seem to be successfull! on Connector call");
                observer.result(false);
            } else {
                observer.result(true);
            }
        }).start();
    }

    public void startSyncing(PlayerFactory playerFactory) throws NullPointerException {
        if (!connector.getChannel().isOpen())
            return;

        startedSyncing = true;

        myPlayerId = connector.getMyId();

        if (connector.isHost()) {
            playerManager = new PlayerManager(playerFactory, this);
            playerManager.addPlayer(myPlayerId);
        }

        synchronizer.setSyncContext(this);

        synchronizer.sync();
    }

    @Override
    public void replicateSyncable(SyncableData data) {
        Class type = idDealer.fromTypeId(data.typeId());

        if (type == null) {
            Log.e("Conductor", "the type: " + data.typeId() + " does not seem to exist");
            return;
        }

        try {
            if (SyncableNodeWrapper.class.isAssignableFrom(type)) {
                Constructor<?> constructor = type.getConstructor(Conductor.class, int.class);
                Object instance = constructor.newInstance(this, data.instanceId());

                try {
                    Syncable newSyncable = (Syncable) instance;
                    newSyncable.giveData(data);

                    idDealer.addIdableInstance(newSyncable, data.instanceId());
                } catch (Exception e) {
                    Log.e("Conductor", e.getMessage() + " happened on Syncable casting");
                }
            }
        } catch (IllegalAccessException e) {
            Log.e("Conductor", "happened on Syncable recreation", e);
        } catch (InstantiationException e) {
            Log.e("Conductor", "happened on Syncable recreation", e);
        } catch (InvocationTargetException e) {
            Log.e("Conductor", "happened on Syncable recreation", e);
        } catch (NoSuchMethodException e) {
            Log.e("Conductor", "happened on Syncable recreation", e);
        }
    }

    @Override
    public void replicateSyncedEvent(SyncedEventData syncedEventData) {
        Class eventType = idDealer.fromTypeId(syncedEventData.eventTypeId());

        if (eventType == null) {
            Log.e("Conductor", "the type: " + syncedEventData.eventTypeId() + " does not seem to exist");
            return;
        }

        if (SyncedEvent.class.isAssignableFrom(eventType)) {
            Idable instance = idDealer.fromInstanceId(syncedEventData.executorId());
            if (instance != null) {
                try {
                    SyncedEventExecutor executor = (SyncedEventExecutor) instance;

                    executor.replicateEvent(syncedEventData);
                } catch (Exception e) {
                    Log.e("Conductor", e.getMessage() + " the instance under the instance id " + syncedEventData.executorId() + " is not a SyncedEventExecutor");
                }
            } else {
                Log.e("Conductor", "the executor of event: " + syncedEventData.eventTypeId() + " does not seem to exist");
            }
        } else {
            Log.e("Conductor", "the type: " + syncedEventData.eventTypeId() + " does not seem to be an event");
        }
    }

    @Override
    public IdDealer getIdDealer() {
        return idDealer;
    }

    @Override
    public Connector getConnector() {
        return connector;
    }

    @Override
    public List<Syncable> getSyncables() {
        return syncables;
    }

    /**
     * Registers a syncable in this.
     *
     * @param syncable
     */
    public void addSyncable(Syncable syncable) {
        if (!syncables.contains(syncable)) {
            syncables.add(syncable);
        }
    }

    /**
     * Unregisters a syncalble in this.
     *
     * @param syncable
     */
    public void removeSyncable(Syncable syncable) {
        syncables.remove(syncable);
    }


    private ActionPackage actionPackage;

    public ActionPackage getActionPackage() {
        return actionPackage;
    }

    @Override
    public void giveActionPackage(ActionPackage clientActionPackage) {
        playerManager.putActionPackage(clientActionPackage);
    }

    public void setActionPackage(ActionPackage actionPackage) throws IllegalArgumentException {
        if (actionPackage == null)
            throw new IllegalArgumentException("the new Actionpackage cannot be null");
        this.actionPackage = actionPackage;
        //send it
        if (startedSyncing) {
            if (synchronizer.isEnforcing()) {
                giveActionPackage(actionPackage);
            } else {
                synchronizer.sendActionPackage(actionPackage);
                actionPackage.resetOnSend();
            }
        }
    }

    @Override
    public void clientJoined(int id) {
        playerManager.addPlayer(id);
    }

    @Override
    public void clientLeft(int id) {
        playerManager.removePlayer(id);
    }

    public Synchronizer synchronizer() {
        return synchronizer;
    }

    @Override
    public void doneUpdating() {
        if (startedSyncing && connector.getChannel().isOpen()) {
            synchronizer.sync();
        }

        performSceneSwitch();
    }
}
