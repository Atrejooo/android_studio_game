package singeltons;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitmapLoader {
    private static BitmapLoader instance;
    private Map<String, Bitmap> bitmapCache; // Cache to store loaded Bitmaps
    private Context context;

    // Private constructor for Singleton pattern
    private BitmapLoader(Context context) {
        this.context = context.getApplicationContext();
        bitmapCache = new HashMap<>();
    }

    // Get the single instance of BitmapLoader
    public static BitmapLoader getInstance(Context context) {
        if (instance == null) {
            instance = new BitmapLoader(context);
        }
        return instance;
    }

    // Load all images from the specified directory (e.g., "assets/images/")
    public void loadImagesFromDirectory(String directory) {
        AssetManager assetManager = context.getAssets();
        Pattern pattern = Pattern.compile("([a-zA-Z]+)_(\\d+)\\.png"); // Regex for imageName_12.png

        try {
            String[] files = assetManager.list(directory);
            if (files != null) {
                for (String filename : files) {
                    Matcher matcher = pattern.matcher(filename);
                    if (matcher.matches()) {
                        String name = matcher.group(1);
                        int number = Integer.parseInt(matcher.group(2));
                        String key = name + "_" + number;

                        // Load bitmap from asset file
                        InputStream is = assetManager.open(directory + "/" + filename);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();

                        // Cache the bitmap
                        bitmapCache.put(key, bitmap);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("BitmapLoader", "Error loading images from directory: " + directory, e);
        }

        for (Map.Entry<String, Bitmap> entry : bitmapCache.entrySet()) {
            Bitmap map = entry.getValue();  // Get the Bitmap value from the entry
            String msg = "loaded: " + entry.getKey();
            Log.d("image_loader", msg);  // Log the Bitmap value
        }
    }

    // Retrieve a Bitmap by name and number
    public Bitmap getBitmap(String name, int number) {
        String key = name + "_" + number;
        return bitmapCache.get(key); // Returns null if the image is not found
    }

    public  Bitmap getBitmap(String fullName) {
        return bitmapCache.get(fullName);
    }

    // Optional: Clear cache if needed
    public void clearCache() {
        for (Bitmap bitmap : bitmapCache.values()) {
            if (bitmap != null) {
                bitmap.recycle(); // Release memory for each bitmap
            }
        }
        bitmapCache.clear();
    }
}
