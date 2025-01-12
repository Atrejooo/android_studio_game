package gameframe.conductors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import gameframe.functionalities.syncing.PlayerFactory;
import gameframe.nodes.SyncableNodeWrapper;
import gameframe.utils.Color;
import synchronizer.ActionPackage;

public class PlayerManager extends Conductible {
    private Map<Integer, ActionPackage> players = new HashMap<>();

    private Map<Integer, SyncableNodeWrapper> playerInstances = new HashMap<>();

    private Map<Integer, Color> playerColors = new HashMap<>();

    private PlayerFactory playerFactory;


    public PlayerManager(PlayerFactory playerFactory, Conductor conductor) {
        this.playerFactory = playerFactory;
        setConductor(conductor);
    }

    public ActionPackage getActionPackageFromId(int playerId) {
        return players.get(playerId);
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
        SyncableNodeWrapper instance = playerInstances.remove(playerId);
        playerColors.remove(playerId);
        if (instance != null) {
            instance.enforceDispose();
        }
    }

    public void addPlayer(int playerId) {
        players.put(playerId, null);
        playerInstances.put(playerId, playerFactory.createPlayerInstance(conductor, playerId));
        playerColors.put(playerId, Color.randomMaxSaturationColor());
    }

    public void putPlayerInstance(int playerId, SyncableNodeWrapper playerInstance) {
        playerInstances.put(playerId, playerInstance);
    }

    public void removePlayerInstance(int playerId){
        playerInstances.remove(playerId);
    }

    public Color getPlayerColor(int id){
        return playerColors.get(id);
    }

    public SyncableNodeWrapper getPlayerInstance(int playerId) {
        return playerInstances.get(playerId);
    }

    public List<SyncableNodeWrapper> getPlayerInstances() {
        List<SyncableNodeWrapper> players = new LinkedList<>();
        for (Integer id : playerInstances.keySet()) {
            SyncableNodeWrapper instance = playerInstances.get(id);
            if (instance != null) {
                players.add(instance);
            }
        }

        return players;
    }

    public int[] playerIds() {
        Set<Integer> keySet = players.keySet();

        // Create an int array with the size of the keyset
        int[] keysArray = new int[keySet.size()];

        // Iterate over the keyset and populate the int array
        int index = 0;
        for (Integer key : keySet) {
            keysArray[index++] = key;
        }

        return keysArray;
    }

    public void reAddPlayers() {
        Map<Integer, SyncableNodeWrapper> map = new HashMap<>();
        for (Integer id : players.keySet()) {
            map.put(id, playerFactory.createPlayerInstance(conductor, id));
        }

        for (Integer id : playerInstances.keySet()) {
            SyncableNodeWrapper instance = playerInstances.get(id);
            if (instance != null) {
                instance.enforceDispose();
            }
        }

        playerInstances = map;
    }

    public SyncableNodeWrapper nextPlayer(int playerInstanceId) {
        boolean next = false;
        SyncableNodeWrapper first = null;
        for (Integer id : playerInstances.keySet()) {
            SyncableNodeWrapper currentInstance = playerInstances.get(id);
            if (currentInstance == null) {
                continue;
            }
            if (first == null) {
                first = currentInstance;
            }
            if (!next) {
                next = playerInstanceId == id;
            } else {
                return currentInstance;
            }
        }
        return first;
    }

    public void putActionPackage(ActionPackage actionPackage) {
        int id = actionPackage.playerId();

        players.put(id, actionPackage);
    }


//    public int registerPlayerInstance(SyncableNodeWrapper instance) {
//        if (playerInstances.containsValue(instance)) {
//            return -1;
//        }
//
//        Set<Integer> keySet = players.keySet();
//
//        // Create an int array with the size of the keyset
//        int[] keysArray = new int[keySet.size()];
//
//        // Iterate over the keyset and populate the int array
//        int index = 0;
//        for (Integer key : keySet) {
//            keysArray[index++] = key;
//        }
//        index = 0;
//        while (playerInstances.containsKey(keysArray[index]) && index < keysArray.length) {
//            index++;
//        }
//
//        playerInstances.put(keysArray[index], instance);
//        return keysArray[index];
//    }
}
