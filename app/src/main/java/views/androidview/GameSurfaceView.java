package views.androidview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Arrays;
import java.util.Comparator;

import common.ICamera;
import common.data.ImgRendererData;
import common.data.RendererData;
import gameframe.functionalities.rendering.Camera;
import gameframe.functionalities.rendering.Renderer;
import gameframe.utils.Vec2;
import singeltons.BitmapLoader;
import views.IView;
import common.data.InputPackage;


public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, IView {
    private Paint paint;

    private Camera cam;
    private Renderer[] renderers;

    public GameSurfaceView(Context context) {
        super(context);

        // Initialize a Paint object for drawing
        paint = new Paint();
        paint.setColor(Color.RED); // Set paint color

        // Set up the SurfaceHolder callback
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle changes here, if needed
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Clean up resources when surface is destroyed, if needed
        BitmapLoader.getInstance(getContext()).clearCache();
    }




    @Override
    public void giveData(ICamera cam, RendererData[] renderers) {
        // Lock the canvas for this frame
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) return;

        try {
            // Clear the canvas only once per frame
            canvas.drawColor(Color.BLACK);

            // Sort renderers based on your comparator
            Arrays.sort(renderers, new Comparator<RendererData>() {
                @Override
                public int compare(RendererData o1, RendererData o2) {
                    int groupComparison = o1.layerGroup.compareTo(o2.layerGroup);
                    return (groupComparison == 0) ? Integer.compare(o1.layer, o2.layer) : groupComparison;
                }
            });

            // Draw each renderer data item
            for (RendererData data : renderers) {
                drawRenderer(cam, data, canvas);
            }
        } catch (Exception e) {
            Log.e("GameSurfaceView", "Error during drawing: " + e.getMessage());
        } finally {
            // Unlock the canvas and post the drawing for this frame
            getHolder().unlockCanvasAndPost(canvas);
        }
    }


    private void drawRenderer(ICamera cam, RendererData data, Canvas canvas) {
        if (cam == null) {
            Log.i("game_view", "camera is null! Rendereing nothing...");
            return;
        }
        if (data instanceof ImgRendererData) {
            try {
                drawImage(cam, (ImgRendererData) data, canvas);
            } catch (Exception e) {
                Log.e("views", e.getMessage());
            }
        }
    }

    private void drawImage(ICamera cam, ImgRendererData data, Canvas canvas) {
        // Get the bitmap from the BitmapLoader
        Bitmap originalBitmap = BitmapLoader.getInstance(getContext()).getBitmap(data.getImgName(), data.getImgNumber());
        if (originalBitmap == null) {
            Log.e("GameSurfaceView", "Bitmap not found: " + data.getImgName() + "_" + data.getImgNumber());
            return;
        }

        Vec2 screen = new Vec2(getWidth(), getHeight());

        // Scale the image according to world space scale and pixel-per-unit ratio
        if (data.scale().x == 0 || data.scale().y == 0)
            return;

        float camPxPerUnit = screen.x / cam.sizeX();

        int scaleX = Math.round(data.scale().x * originalBitmap.getWidth() / data.pxPerUnit() * camPxPerUnit);
        int scaleY = Math.round(data.scale().y * originalBitmap.getHeight() / data.pxPerUnit() * camPxPerUnit);
        // Resize the bitmap based on scale
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,
                (int) (scaleX),
                (int) (scaleY),
                true);

        // Create a matrix for transformations
        Matrix matrix = new Matrix();

        // Set pivot point relative to the scaled bitmap dimensions
        int centerX = Math.round(scaleX * data.center().x);
        int centerY = Math.round(scaleY * data.center().y);

        // Apply rotation around the pivot point
        matrix.postRotate((float) Math.toDegrees(data.roation), centerX, centerY);


        // Move the image to its screen position, adjusting for the pivot offset
        Vec2 screenPos = cam.world2ScreenPoint(data.pos(), screen);
        matrix.postTranslate(screenPos.x - centerX, screenPos.y - centerY);

        // Draw the transformed bitmap on the canvas
        canvas.drawBitmap(scaledBitmap, matrix, null);
    }


    @Override
    public InputPackage getInputPackage() {
        return new InputPackage(new Vec2[0], null, null);
    }
}
