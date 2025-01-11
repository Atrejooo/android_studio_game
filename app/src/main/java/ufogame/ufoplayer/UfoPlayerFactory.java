package ufogame.ufoplayer;

import gameframe.conductors.Conductor;
import gameframe.functionalities.syncing.PlayerFactory;
import gameframe.nodes.SyncableNodeWrapper;

public class UfoPlayerFactory implements PlayerFactory {
    @Override
    public SyncableNodeWrapper createPlayerInstance(Conductor conductor, int playerId) {
        UfoPlayerWrapper wrapper = new UfoPlayerWrapper(conductor);
        wrapper.setPlayerId(playerId);

        return wrapper;
    }
}
