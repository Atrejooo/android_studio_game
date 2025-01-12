package views;

import android.content.Context;

public class ViewFactory {
    public static IView createView(Context context) {
        return new GameGLSurfaceView(context);
    }
}
