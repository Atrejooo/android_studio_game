package ufogame.ufoplayer;

import java.util.LinkedList;
import java.util.List;

import gameframe.conductors.Conductor;
import gameframe.conductors.Yell;
import gameframe.functionalities.movement.VelocityMovement;
import gameframe.nodes.Node;
import gameframe.nodes.NodeWrapper;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.utils.Vec2;
import synchronizer.SyncedEvent;
import synchronizer.SyncedEventData;
import ufogame.ball.BallWrapper;

public class UfoShockWaveSyncedEvent implements SyncedEvent {
    private List<Integer> objectIds = new LinkedList<Integer>();
    private List<Vec2> velos = new LinkedList<>();
    private int executorId;

    private float minSpeed = 10;

    public UfoShockWaveSyncedEvent(Conductor conductor, int executorId, int playerId, VelocityMovement playerMovement, List<Node> nodes) {
        typeId = conductor.getIdDealer().typeToTypeId(UfoShockWaveSyncedEvent.class);
        this.executorId = executorId;

        for (Node node : nodes) {
            VelocityMovement velocityMovement = node.get(VelocityMovement.class);

            if (velocityMovement != null) {
                Vec2 oldVelo = velocityMovement.velo();

                Vec2 newVelo = oldVelo.mult(-1).add(playerMovement.velo()).clampMag(minSpeed, Float.MAX_VALUE);

                NodeWrapper nodeWrapper = node.getNodeWrapper();
                if (nodeWrapper != null && nodeWrapper instanceof SyncableNodeWrapper) {
                    int nodeWrapperId = ((SyncableNodeWrapper) nodeWrapper).instanceId();
                    if (nodeWrapperId != executorId) {
                        velocityMovement.setVelo(newVelo);
                        objectIds.add(nodeWrapperId);
                        velos.add(newVelo);
                    }

                    if (nodeWrapper instanceof BallWrapper) {
                        BallWrapper ballWrapper = (BallWrapper) nodeWrapper;

                        conductor.yell(Yell.SWITCH_TARGET);
                    }
                } else {
                    velocityMovement.setVelo(newVelo);
                }
            }
        }
    }

    @Override
    public SyncedEventData getData() {
        UfoShockWaveSyncedData data = new UfoShockWaveSyncedData();

        data.eventTypeId = typeId;

        data.objectIds = new int[objectIds.size()];
        int index = 0;
        for (int id : objectIds) {
            data.objectIds[index] = id;
            index++;
        }

        data.newVelos = new Vec2[velos.size()];
        index = 0;
        for (Vec2 velo : velos) {
            data.newVelos[index] = velo;
            index++;
        }

        data.executorId = executorId;

        return data;
    }

    int typeId;

    @Override
    public int typeId() {
        return typeId;
    }

    @Override
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public int instanceId() {
        return 0;
    }

    @Override
    public void setInstanceId(int instanceId) {

    }
}
