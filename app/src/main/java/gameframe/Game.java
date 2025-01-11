package gameframe;

import gameframe.conductors.Conductor;
import gameframe.conductors.DoneUpdateable;
import gameframe.scenes.Scene;
import gameframe.utils.ConnectionSource;
import views.IView;

/**
 * Represents an instance of a games logic.
 */
public class Game implements DoneUpdateable {
    private Conductor conductor;
    private Scene[] scenes;
    private IView view;


    /**
     * Creates a new {@code Game} object/holder for game functionality with an array of {@code Scene} objects.
     *
     * @param scenes the scenes that are supposed to be in the game
     * @throws NullPointerException     if the scenes array is null
     * @throws IllegalArgumentException if the scenearray or one of the scenearrays entries is null
     */
    public Game(Scene[] scenes, String deviceIp) throws IllegalArgumentException {
        if (scenes == null) {
            throw new IllegalArgumentException("the scene array is null");
        }
        if (deviceIp == null)
            throw new IllegalArgumentException("deviceIp is null");

        for (Scene scene : scenes) {
            if (scene == null) {
                throw new IllegalArgumentException("an entry in the scene array is null");
            }
        }

        this.scenes = scenes;

        // build a conductor
        conductor = new Conductor(this, scenes);

        ConnectionSource connectionSource = new ConnectionSource();
        connectionSource.ip = deviceIp;
        conductor.setConnectionSource(connectionSource);

        conductor.setFps(100);

        // init loop
        conductor.start();
    }

    public void setView(IView view) {
        if (view != null)
            this.view = view;
    }

    @Override
    public void doneUpdating() {
        if (view != null) {
            view.giveData(conductor.camera(), conductor.getRendererDatas());
            conductor.setInput(view.getInputPackage());
        }

        // print input debugging
        // Log.d("Game", Arrays.toString(conductor.input().points()));
    }
}
