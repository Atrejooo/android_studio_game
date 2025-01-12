package ufogame.gamemanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.data.TxtRendererData;
import gameframe.conductors.PlayerManager;
import gameframe.functionalities.movement.CameraAnchor;
import gameframe.functionalities.rendering.LayerGroup;
import gameframe.functionalities.rendering.TxtRenderer;
import gameframe.nodes.Node;
import gameframe.conductors.Conductor;
import gameframe.conductors.Yell;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.utils.Delay;
import gameframe.utils.Vec2;
import synchronizer.SyncableData;
import ufogame.ball.BallWrapper;
import ufogame.ufoplayer.UfoPlayerWrapper;

public class GameManager extends SyncableNodeWrapper {

    private BallWrapper ball;

    public GameManager(Conductor conductor) {
        super(conductor);
    }

    public GameManager(Conductor conductor, int instanceId) {
        super(conductor, instanceId);
    }

    Map<Integer, PlayerInfo> playerInfos = new HashMap<Integer, PlayerInfo>();

    @Override
    protected void init() {
        syncDelay = 5;

        if (!conductor.synchronizer().isEnforcing())
            return;
        playerInfos = new HashMap<Integer, PlayerInfo>();
        updatePlayerInfo();
        createBall();

        performSync = true;
    }

    @Override
    protected void processData(SyncableData data) {
        GameManagerSyncableData gameData = (GameManagerSyncableData) data;

        playerInfos = gameData.playerInfos;

        createLayout();
    }

    @Override
    public SyncableData getData() {
        GameManagerSyncableData data = new GameManagerSyncableData();

        data.playerInfos = playerInfos;

        data.setInstanceId(instanceId());
        data.setTypeId(typeId());

        return data;
    }

    private Delay updateDelay = new Delay(2);

    @Override
    protected void enforcerUpdate() {
        if (updateDelay.use(conductor.delta())) {
            updatePlayerInfo();
        }
    }

    private boolean performSync;

    @Override
    protected boolean defineSyncingCondition() {
        boolean remember = performSync;
        performSync = false;
        return remember;
    }

    private void createLayout() {
        disposeNodes();

        float spacing = 0.05f;

        int i = 0;
        for (int key : playerInfos.keySet()) {
            PlayerInfo playerInfo = playerInfos.get(key);

            Node node = conductor.activeScene().add(new Vec2(), "score points");
            addNode(node);

            TxtRendererData txtRendererData = node.add(TxtRenderer.class).data();
            txtRendererData.setText("" + playerInfo.score);
            txtRendererData.setStyle(1);
            txtRendererData.setTextSize(40);
            txtRendererData.setCenter(new Vec2());
            txtRendererData.setColor(playerInfo.color);
            txtRendererData.layerGroup = LayerGroup.UI;

            CameraAnchor anchor = node.add(CameraAnchor.class);
            anchor.setAnchor(new Vec2(0, i * spacing));
            anchor.setScale(new Vec2(0.3f));

            i++;
        }
    }

    private void createBall() {
        if (ball != null)
            ball.enforceDispose();
        ball = new BallWrapper(conductor);
        ball.setTarget(conductor.playerManager().getPlayerInstance(conductor.getMyPlayerId()));
    }

    private void updatePlayerInfo() {
        int[] ids = conductor.playerManager().playerIds();

        for (int id : ids) {
            PlayerInfo playerInfo = playerInfos.get(id);
            if (playerInfo == null) {
                playerInfos.put(id, new PlayerInfo(id, conductor.playerManager().getPlayerColor(id)));
            }
        }

        LinkedList<Integer> toRemove = new LinkedList<>();
        for (int key : playerInfos.keySet()) {
            boolean contained = false;
            for (int id : ids) {
                if (id == key) {
                    contained = true;
                }
            }
            if (!contained) {
                toRemove.add(key);
            }
        }
        while (toRemove.size() > 0) {
            playerInfos.remove(toRemove.removeFirst());
        }
        performSync = true;

        createLayout();
    }

    @Override
    public void onYell(Yell yell) {
        if (yell == Yell.DIED) {
            PlayerManager playerManager = conductor.playerManager();

            List<SyncableNodeWrapper> instances = playerManager.getPlayerInstances();
            if (instances.size() < 2) {

                if (instances.size() == 1) {
                    int survivor = ((UfoPlayerWrapper) instances.get(0)).getPlayerId();
                    addScore(survivor);
                }
                restart();
            }
        }
    }

    private void restart() {
        PlayerManager playerManager = conductor.playerManager();
        playerManager.reAddPlayers();
        createBall();
        performSync = true;
    }

    private void addScore(int playerId) {
        PlayerInfo info = playerInfos.get(playerId);
        if (info == null)
            return;

        info.score++;
        performSync = true;
        createLayout();
    }
}
