package ufogame.gamemanager;

import java.io.Serializable;

import gameframe.utils.Color;

public class PlayerInfo implements Serializable {
    public PlayerInfo(int id, Color color) {
        this.id = id;
        this.color = color;
    }

    int id;
    int score;
    Color color;
}

