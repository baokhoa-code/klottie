package com.example.androidlottieapp;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class KLottieOptions {

    public final static int REPEAT_MODE_INFINITE = -1;
    public final static int REPEAT_MODE_NONE = 0;
    static final int DEFAULT = -100;
    List<KLottieProperty.PropertyUpdate> properties = null;
    BuilderType type;
    String json;
    int w = DEFAULT, h = DEFAULT;
    boolean startDecode = true;
    int autoRepeat = REPEAT_MODE_INFINITE;
    KLottieMarker selectedMarker = null;
    float speed = 1f;
    String cacheName;
    public KLottieOptions(String json, String cacheName) {
        if (TextUtils.isEmpty(json))
            throw new NullPointerException("json can't be empty!");
        if (TextUtils.isEmpty(cacheName))
            throw new NullPointerException("cacheName can't be empty!");
        this.json = json;
        this.cacheName = cacheName;
        this.type = BuilderType.JSON;
    }

    public KLottieOptions setCacheName(String cacheName) {
        if (TextUtils.isEmpty(cacheName)) {
            if (TextUtils.isEmpty(cacheName))
                throw new NullPointerException("lottie name (cacheName) can not be null!");
            else
                return this;
        }
        this.cacheName = cacheName;
        return this;
    }

    public KLottieOptions setSize(int w, int h) {
        if (w <= 0 || h <= 0) {
            throw new RuntimeException("lottie width and height must be > 0");
        }
        this.w = w;
        this.h = h;
        return this;
    }

    public KLottieOptions setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public KLottieOptions setAllowDecodeSingleFrame(boolean startDecode) {
        this.startDecode = startDecode;
        return this;
    }

    public KLottieOptions addLayerProperty(String keyPath, KLottieProperty property) {
        if (properties == null) properties = new ArrayList<>();
        properties.add(new KLottieProperty.PropertyUpdate(property, keyPath));
        return this;
    }

    public KLottieOptions setSelectedMarker(KLottieMarker marker) {
        this.selectedMarker = marker;
        return this;
    }

    public KLottieOptions setRepeat(int repeatCount) {
        autoRepeat = repeatCount;
        return this;
    }

    public KLottieOptions setRepeat(boolean enabled) {
        return setRepeat(enabled ? REPEAT_MODE_INFINITE : REPEAT_MODE_NONE);
    }

    public KLottieDrawable build(KLottieDrawable.KLottieDrawableListener listener) {
        return new KLottieDrawable(this, listener);
    }

    public enum BuilderType {
        JSON, FILE, URL
    }
}
