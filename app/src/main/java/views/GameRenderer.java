package views;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gameframe.utils.Vec2;

class GameRenderer implements GLSurfaceView.Renderer {
    /**
     * max allowed vertices (* 4)
     */
    public final int verticesCap = 16384 * 8;
    /**
     * context object of the GlSurfaceView
     */
    private final Context context;

    // buffer references
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private FloatBuffer colorBuffer;

    /**
     * list of Bitmaps per 4 vertices
     */
    private ArrayList<Bitmap> bitmaps;

    /**
     * shader program handle
     */
    private int program;

    /**
     * handle for vertex position in shader
     */
    private int positionHandle;
    /**
     * texture coordinate handle in shader
     */
    private int texCoordHandle;
    /**
     * handle for the texture of current quad
     */
    private int textureHandle;

    /**
     * handle for the color attribute in the frag shader
     */
    private int colorHandle;

    /**
     * saves the context
     *
     * @param context
     */
    public GameRenderer(Context context) {
        this.context = context;
    }

    /**
     * array of points/vertices -> 8 values per quad (x, y * 4)
     */
    private float[] vertices = {
            -1.0f, 1.0f,    // Top-left
            1.0f, 1.0f,     // Top-right
            -1.0f, -1.0f,   // Bottom-left
            1.0f, -1.0f     // Bottom-right
    };
    /**
     * boarder around the texture (offset to the texture coords.) to avoid clipping
     */
    private static final float boarder = 0.001f;
    /**
     * the standard texture coordinates that each quad uses
     */
    float[] standardTexCoords = {
            0.0f + boarder, 0.0f + boarder,
            1.0f - boarder, 0.0f + boarder,
            0.0f + boarder, 1.0f - boarder,
            1.0f - boarder, 1.0f - boarder
    };

    /**
     * array of colors as floats -> with 4 colors per quad -> 1 color per vertex (as r, g, b, a)
     */
    private float[] colors = new float[]{
            1, 1, 1, 1,
            1, 1, 1, 1,
            1, 1, 1, 1,
            1, 1, 1, 1
    };

    /**
     * current texture coordinates/UVs -> usually just a repetition of {@code standardTexCoords}, once per quad
     */
    private float[] textureUVs;

    /**
     * takes the prepared data in order to render vertices.lenght/8 quads
     * puts the data in the right format and saves it to buffers
     * only uses the data if the Renderer finished Rendering ({@code isRendering} == false)
     *
     * @param vertices array holding all vertices
     * @param bitmaps  List of bitmaps (1 per 8 vertices)
     * @param colors   array of colors (r, g, b, a) * 4 per quad (per vertex)
     */
    public void giveData(float[] vertices, ArrayList<Bitmap> bitmaps, float[] colors) {
        if (!created || isRendering)
            return;

        this.vertices = vertices;
        this.bitmaps = bitmaps;
        this.colors = colors;

        float[] textureUVs = new float[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            textureUVs[i] = standardTexCoords[i % 8];
        }


        //reallocate a new memory for the new data
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4) // Allocate new buffer
                .order(ByteOrder.nativeOrder())  // Use native order
                .asFloatBuffer();
        vertexBuffer.put(vertices); // Populate the buffer with the new data
        vertexBuffer.position(0); // Reset position to 0 to prepare for use

        texCoordBuffer = ByteBuffer.allocateDirect(textureUVs.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordBuffer.put(textureUVs).position(0);

        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.put(colors).position(0);
    }

    /**
     * keep track if the initial code was called
     */
    private boolean created;

    //initializes the shader and glContext...
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        created = true;

        // Set the clear color to black
        GLES20.glClearColor(0.06f, 0.0f, 0.12f, 1.0f); // Black (R=0, G=0, B=0, A=1)

        // Enable blending for transparency
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Create shader program
        program = createProgram();
        GLES20.glUseProgram(program);

        // Get handles to attributes and uniforms
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord");
        textureHandle = GLES20.glGetUniformLocation(program, "u_Texture");
        colorHandle = GLES20.glGetAttribLocation(program, "a_Color");


        // Initialize buffers (allows 512 quads to be drawn on the screen)
        vertexBuffer = ByteBuffer.allocateDirect(verticesCap * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(standardTexCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordBuffer.put(standardTexCoords).position(0);

        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.put(colors).position(0);
    }

    /**
     * current aspect of the renderer
     *
     * @return aspect ratio as float
     */
//    public float aspectRatio() {
//        return aspect;
//    }
    public float aspectRatio() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        aspect = (float) metrics.heightPixels / (float) metrics.widthPixels;
        return aspect;
    }

    /**
     * current dimensions of the screen
     *
     * @return Vec2(width, height)
     */
    public Vec2 screen() {
        return new Vec2(width, height);
    }

    /**
     * current aspect, is calculated when the surface changes
     */
    private float aspect;
    /**
     * dimensions of the screen
     */
    private int width, height;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Adjust the viewport based on geometry changes
        GLES20.glViewport(0, 0, width, height);

        aspect = (float) height / (float) width;

        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the color buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        drawTexturedQuad();
    }

    /**
     * loads shader into GLE
     */
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * tells OpenGL to load the shaders and create a pipeline with them
     *
     * @return the program handle of the created program
     */
    private int createProgram() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }

    //    private void drawTexturedQuad() {
//        if (bitmaps == null)
//            return;
//
//        GLES20.glUseProgram(program);
//
//        // Enable vertex attribute arrays
//        GLES20.glEnableVertexAttribArray(positionHandle);
//        GLES20.glEnableVertexAttribArray(texCoordHandle);
//
//        // Prepare vertex data
//        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//
//        texCoordBuffer.position(0);  // Start at the beginning of the texCoordBuffer
//        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
//
//        for (int i = 0; i < vertices.length / 8; i++) {
//            Bitmap bitmap = bitmaps.get(Math.min(i, bitmaps.size() - 1));
//            int textureId = getTextureId(bitmap);
//            if (textureId == -1) {
//                continue;
//            }
//
//            // Bind the texture
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//            GLES20.glUniform1i(textureHandle, 0);
//
//            // Draw the quad as a triangle strip
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * 4, 4);
//        }
//        // Disable vertex arrays after drawing
//        GLES20.glDisableVertexAttribArray(positionHandle);
//        GLES20.glDisableVertexAttribArray(texCoordHandle);
//    }

    /**
     * keeps track of if {@code this} is currently in {@code drawTexturedQuad()}
     * to avoid data changes when rendering
     */
    private boolean isRendering;

    /**
     * tells GLE to use the correct buffers for the correct shader attributes
     * loops over each quad
     * rebinds the texture for each quad
     * then draws the quad as a triangle strip
     */
    private void drawTexturedQuad() {
        if (bitmaps == null || isRendering)
            return;

        isRendering = true;

        GLES20.glUseProgram(program);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
        colorBuffer.position(0);  // Ensure position is set to the start of the color buffer
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        for (int i = 0; i < vertices.length / 8; i++) {
            Bitmap bitmap = bitmaps.get(Math.min(i, bitmaps.size() - 1));
            int textureId = getTextureId(bitmap);
            if (textureId == -1) continue;

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(textureHandle, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * 4, 4);
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);

        isRendering = false;
    }


    /**
     * keep track of which textures OpenGl has already in cache
     */
    private ArrayList<GameRenderer.LoadedTexture> loaded = new ArrayList<GameRenderer.LoadedTexture>();

    /**
     * returns the texture handle of the Bitmap that was passed to OpenGl
     * considers if the texture was already loaded
     *
     * @param bitmap to load
     * @return the according texture handle
     */
    public int getTextureId(Bitmap bitmap) {
        for (GameRenderer.LoadedTexture texture : loaded) {
            if (texture.bitmap == bitmap) {
                return texture.id;
            }
        }
        if (bitmap != null) {
            int id = -1;
            try {
                id = loadTexture(bitmap);
                if (id != 0) {
                    loaded.add(new GameRenderer.LoadedTexture(bitmap, id));
                } else {
                    return -1;
                }
            } catch (Exception e) {
                Log.e("GameRenderer", e.getMessage());
            }
            return id;
        }
        return -1;
    }

    /**
     * loads the bitmap as a texture in OpenGL
     *
     * @param bitmap to be loaded
     * @return id
     */
    private int loadTexture(Bitmap bitmap) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering options
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        } else {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


    private class LoadedTexture {
        public Bitmap bitmap;
        public int id;

        public LoadedTexture(Bitmap bitmap, int id) {
            this.bitmap = bitmap;
            this.id = id;
        }
    }

    /**
     * vertex shader code used for drawing quads
     */
    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 a_Position;" +
                    "attribute vec2 a_TexCoord;" +
                    "attribute vec4 a_Color;" +  // New attribute for color modifier
                    "varying vec2 v_TexCoord;" +
                    "varying vec4 v_Color;" +    // Pass color to fragment shader
                    "void main() {" +
                    "    gl_Position = a_Position;" +
                    "    v_TexCoord = a_TexCoord;" +
                    "    v_Color = a_Color;" +    // Pass color modifier to fragment shader
                    "}";


    /**
     * fragment shader code used for drawing quads
     */
    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoord;" +
                    "varying vec4 v_Color;" +  // Receive color modifier
                    "void main() {" +
                    "    vec4 texColor = texture2D(u_Texture, v_TexCoord);" +
                    "    gl_FragColor = texColor * v_Color;" +  // Apply color modifier
                    "}";
}
