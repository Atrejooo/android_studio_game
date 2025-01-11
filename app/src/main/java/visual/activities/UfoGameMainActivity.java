package visual.activities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import gameframe.scenes.Scene;
import gameframe.testUtils.TestScene;
import ufogame.scenes.UfoSceneSupplier;
import gameframe.Game;

import singeltons.BitmapLoader;
import views.openglview.GameGLSurfaceView;

public class UfoGameMainActivity extends AppCompatActivity {
    private GameGLSurfaceView glSurfaceView;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set app to fullscreen
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BitmapLoader.getInstance(this).loadImagesFromDirectory("imgs");
        Log.i("main", "hi :)");


        String deviceIp = getLocalIpAddress(this);
        Log.d("main", deviceIp);

        //game = new Game(new Scene[]{new TestScene()}, "10.0.2.2");
        game = new Game(UfoSceneSupplier.getScenes(), "10.0.2.2");

        createView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null)
            glSurfaceView.onPause(); // Pause OpenGL rendering when activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        createView();

        if (glSurfaceView != null)
            glSurfaceView.onResume(); // Resume OpenGL rendering when activity resumes
    }

    public String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        }
        return "Unable to fetch IP Address";
    }

    private void createView() {
        if (game != null) {
            glSurfaceView = new GameGLSurfaceView(this);
            setContentView(glSurfaceView);
            glSurfaceView.setPreserveEGLContextOnPause(true);
            game.setView(glSurfaceView);
        }
    }
}
