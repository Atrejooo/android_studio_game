package visual.activities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import connector.Connector;
import connector.ConnectorFactory;
import gameframe.Game;
import singeltons.BitmapLoader;
import views.openglview.GameGLSurfaceView;


public class MainHostActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate(savedInstanceState, true);
    }
}
