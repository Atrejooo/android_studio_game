package gameframe.functionalities.syncing;

import gameframe.conductors.Conductor;
import gameframe.nodes.SyncableNodeWrapper;

public interface PlayerFactory {
    SyncableNodeWrapper createPlayerInstance(Conductor conductor, int playerId);
}
