package ufogame.gamemanager;

import gameframe.conductors.Conductible;
import gameframe.conductors.Conductor;

public class GameManager extends Conductible {
    public GameManager(Conductor conductor){
        setConductor(conductor);
    }
}
