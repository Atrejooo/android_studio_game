package ufogame.scenes;

import gameframe.scenes.Scene;

public class UfoSceneSupplier {
    public static Scene[] getScenes() {
        return new Scene[]{
                new MenuScene(),
                new GameScene(),
        };
    }
}
