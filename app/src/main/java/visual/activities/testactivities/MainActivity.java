package visual.activities.testactivities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.format.Formatter;
import android.view.WindowManager;

import connector.*;
import gameframe.*;

import android.util.Log;

import singeltons.BitmapLoader;
import views.IView;
import views.ViewFactory;


public abstract class MainActivity extends AppCompatActivity {
    private IView glSurfaceView;
    private Game game;
    private Connector connector;


    protected void onCreate(Bundle savedInstanceState, boolean open) {
        //set app to fullscreen
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

        connector = ConnectorFactory.createConnector();
        int port = 54321;
        if (open) {
            new Thread(() -> {
                connector.open(port);
            }).start();
        } else {
            new Thread(() -> {
                connector.connect("10.0.2.2", port);
            }).start();
        }


//        boolean bad = false;
//        if (!bad) {
//            game = new Game(new Scene[]{new TestScene()});
//
//            createView();
//        } else {
//            GameSurfaceView surfaceView = new GameSurfaceView(this);
//            setContentView(surfaceView);
//            new Game(new Scene[]{new TestScene()}).setView(surfaceView);
//        }
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
            glSurfaceView = ViewFactory.createView(this);
            setContentView((GLSurfaceView) glSurfaceView);
            game.setView(glSurfaceView);
        }
    }
}
