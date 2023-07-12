package com.example.androidlottieapp;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import com.example.androidlottieapp.extensions.JsonFileExtension;
import com.example.androidlottieapp.extensions.KFileExtension;
import com.getkeepsafe.relinker.ReLinker;

import java.util.HashMap;
import java.util.Map;


public class KLottie {

    private static final Map<String, KFileExtension> supportFileExtensions = new HashMap<>();
    static Context applicationContext;
    static float screenRefreshRate = 60;

    public static void init(Context context) {
        ReLinker.loadLibrary(context, "jlottie");
        KLottie.applicationContext = context.getApplicationContext();
        getScreenRefreshRate(context);
        addFileExtension(JsonFileExtension.JSON);
    }

    public static Map<String, KFileExtension> getSupportedFileExtensions() {
        return supportFileExtensions;
    }

    public static void addFileExtension(KFileExtension fileExtension) {
        supportFileExtensions.put(fileExtension.extension.toLowerCase(), fileExtension);
    }

    public static void getScreenRefreshRate(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            Display display = manager.getDefaultDisplay();
            if (display != null) {
                screenRefreshRate = display.getRefreshRate();
            }
        }
    }

    public static float getScreenRefreshRate() {
        return screenRefreshRate;
    }

}
