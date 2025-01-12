package views;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import common.ICamera;
import common.data.ImgRendererData;
import common.data.InputPackage;
import common.data.RendererData;
import common.data.TxtRendererData;
import gameframe.utils.Color;
import gameframe.utils.Range;
import gameframe.utils.Vec2;
import singeltons.BitmapLoader;
import singeltons.PerformanceTester;
import singeltons.TextBitmapLoader;

class GameGLSurfaceView extends GLSurfaceView implements IView {
    private GameRenderer gameRenderer;
    private ICamera cam;
    private InputPackage input = new InputPackage(new Vec2[0], null, null);


    public GameGLSurfaceView(Context context) {
        super(context);

        // Set OpenGL ES version
        setEGLContextClientVersion(2);  // Using OpenGL ES 2.0
        setPreserveEGLContextOnPause(true);

        // Set the custom renderer
        gameRenderer = new GameRenderer(getContext());
        setRenderer(gameRenderer);
        // Set render mode to continuous rendering (alternatively, use RENDERMODE_WHEN_DIRTY if frame-by-frame updates aren't necessary)
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void giveData(ICamera cam, RendererData[] renderers) {


        Arrays.sort(renderers, new Comparator<RendererData>() {
            @Override
            public int compare(RendererData o1, RendererData o2) {
                int groupComparison = o1.layerGroup.compareTo(o2.layerGroup);
                return (groupComparison == 0) ? Integer.compare(o1.layer, o2.layer) : groupComparison;
            }
        });

        try {
            prepData(cam, renderers);
        } catch (Exception e) {
            Log.e("GameSurfaceView", "Error during rendering: " + e.getMessage());
        }
    }

    private void prepData(ICamera cam, RendererData[] renderers) {
        this.cam = cam;

        if (paused) return;

        ArrayList<Float> verts = new ArrayList<Float>();
        ArrayList<Float> colors = new ArrayList<Float>();

        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

        float aspect = gameRenderer.aspectRatio();

        if (aspect == 0) {
            return;
        }

        for (int i = 0; i < renderers.length && i < gameRenderer.verticesCap / 8; i++) {
            RendererData data = renderers[i];

            Bitmap bitmap = retriveBitmap(data);

            if (bitmap == null)
                continue;

            Vec2 boaders = new Vec2(bitmap.getWidth() / data.pxPerUnit(), bitmap.getHeight() / data.pxPerUnit());

            Vec2 a = new Vec2(-data.center().x, data.center().y).mult(data.scale()).mult(boaders).rotate(data.roation).add(data.pos());
            Vec2 b = new Vec2(1.0f - data.center().x, data.center().y).mult(data.scale()).mult(boaders).rotate(data.roation).add(data.pos());
            Vec2 c = new Vec2(1.0f - data.center().x, data.center().y - 1.0f).mult(data.scale()).mult(boaders).rotate(data.roation).add(data.pos());
            Vec2 d = new Vec2(-data.center().x, data.center().y - 1.0f).mult(data.scale()).mult(boaders).rotate(data.roation).add(data.pos());

            Vec2[] points = new Vec2[]{
                    a, b, d, c
            };

            boolean cull = true;

            Range range = new Range(-1f, 1f);
            for (int j = 0; j < points.length; j++) {
                Vec2 mappedPoint = cam.map(points[j], aspect);
                if (cull && range.inside(mappedPoint.x) && range.inside(mappedPoint.y)) {
                    cull = false;
                }
                points[j] = mappedPoint;
            }
            if (cull)
                continue;

            bitmaps.add(bitmap);

            for (int j = 0; j < points.length; j++) {
                verts.add(points[j].x);
                verts.add(points[j].y);
            }


            //4 times for each vert
            Color color = data.color();
            for (int j = 0; j < 4; j++) {
                colors.add(color.r());
                colors.add(color.g());
                colors.add(color.b());
                colors.add(color.a());
            }
        }

        float[] vertsArray = toArr(verts);

        float[] colorArray = toArr(colors);

        PerformanceTester.passRendereingData(vertsArray.length);

        gameRenderer.giveData(vertsArray, bitmaps, colorArray);

        requestRender();
    }

    private Bitmap retriveBitmap(RendererData data) {
        if (data instanceof ImgRendererData) {
            try {
                ImgRendererData imgRendererData = (ImgRendererData) data;
                return BitmapLoader.getInstance(getContext()).getBitmap(imgRendererData.getImgName(), imgRendererData.getImgNumber());
            } catch (Exception e) {
                Log.e("GameGlSurfaceView", e.getMessage());
                return null;
            }
        } else if (data instanceof TxtRendererData) {
            TxtRendererData txtRendererData = (TxtRendererData) data;
            return TextBitmapLoader.getInstance().getTextBitmap(txtRendererData.text(),
                    txtRendererData.textSize(), txtRendererData.fontName(), txtRendererData.style());
        }
        return null;
    }

    private float[] toArr(ArrayList<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    @Override
    public InputPackage getInputPackage() {
        return input;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        boolean isRelease = event.getAction() == MotionEvent.ACTION_POINTER_UP || event.getAction() == MotionEvent.ACTION_UP;

        Vec2[] points = new Vec2[isRelease ? pointerCount - 1 : pointerCount];

        int releasedActionReached = 0;

        try {
            for (int i = 0; i < pointerCount; i++) {
                if (!isRelease || isRelease && event.getActionIndex() != i) {
                    Vec2 screenPoint = new Vec2(event.getX(i), event.getY(i));
                    points[i - releasedActionReached] = screenPoint;
                } else {
                    releasedActionReached = 1;
                }
            }
        } catch (Exception e) {
            Log.e("GameGLSurface", e.getMessage());
        }


        input = new InputPackage(points, cam, gameRenderer.screen());

        return true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Clean up resources when surface is destroyed, if needed

    }


    private boolean paused;

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }


}

