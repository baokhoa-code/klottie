package com.example.androidlottieapp;

import android.app.Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KLottieApplication extends Application {

    public static List<KLottieItem> mItem = new ArrayList<>();
    public static KlottieItemAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        KLottie.init(this);

        try {
            String[] fileNames = getAssets().list("");
            for (String fileName : fileNames) {
                if (fileName.endsWith(".json")) {
                    String jsonFileName = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (!jsonFileName.equals("sfpconfig")) {
                        KLottieDrawable drawable = KLottieDrawable.fromAssets(getApplicationContext(), fileName, jsonFileName)
                                .setSize(128, 128)
                                .setSpeed(1f)
                                .setRepeat(true)
                                .build(new KLottieDrawable.KLottieDrawableListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onStop() {

                                    }

                                    @Override
                                    public void onProgress(int frame) {

                                    }
                                });
                        mItem.add(new KLottieItem(fileName, jsonFileName, drawable));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter = new KlottieItemAdapter(mItem);

    }
}
