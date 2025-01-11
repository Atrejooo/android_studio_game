package singeltons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class TextBitmapLoader {
    private static TextBitmapLoader instance;
    private final Map<String, Bitmap> bitmapCache;

    // Private constructor to prevent direct instantiation
    private TextBitmapLoader() {
        bitmapCache = new HashMap<>();
    }

    // Public method to get the singleton instance
    public static synchronized TextBitmapLoader getInstance() {
        if (instance == null) {
            instance = new TextBitmapLoader();
        }
        return instance;
    }

    /**
     * Generates or retrieves a cached Bitmap for the given text.
     *
     * @param text      The text to render.
     * @param textSize  The size of the text.
     * @param fontName  The name of the font (e.g., "sans-serif-condensed").
     * @param style     The style of the font (e.g., Typeface.BOLD).
     * @return A Bitmap containing the rendered text.
     */
    public Bitmap getTextBitmap(String text, float textSize, String fontName, int style) {
        // Create a unique key for caching based on text and font settings
        String cacheKey = text + "|" + textSize + "|" + fontName + "|" + style;

        // Check if the Bitmap for the given key is already cached
        if (bitmapCache.containsKey(cacheKey)) {
            return bitmapCache.get(cacheKey);
        }

        // Generate the Typeface from the font name and style
        Typeface typeface = Typeface.create(fontName, style);

        // Generate a new Bitmap
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(Color.WHITE);
        paint.setTypeface(typeface);

        // Measure text dimensions
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        int width = textBounds.width() + 20; // Add padding
        int height = textBounds.height() + 20; // Add padding

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0x00000000); // Transparent background

        // Draw the text
        canvas.drawText(text, 10, height - 10, paint);

        // Cache the generated Bitmap
        bitmapCache.put(cacheKey, bitmap);

        return bitmap;
    }
}

