package com.example.androidlottieapp;

import android.graphics.Bitmap;

class KLottieNative {

    public static native long createInfoByJson(String json, String name, int[] params);

    public static native long getFramerate(long ptr);

    public static native void releaseInfo(long ptr);

    public static native int getFrame(long ptr, int frame, Bitmap bitmap, int w, int h, int stride);

    public static native int getMarkersCount(long ptr);

    public static native String[] getMarkerData(long ptr, int index);

    public static native int getLayersCount(long ptr);

    public static native String[] getLayerData(long ptr, int index);

    public static native void setLayerColor(long ptr, String layer, int color);

    public static native void setLayerStrokeColor(long ptr, String layer, int color);

    public static native void setLayerFillOpacity(long ptr, String layer, float color);

    public static native void setLayerStrokeOpacity(long ptr, String layer, float value);

    public static native void setLayerStrokeWidth(long ptr, String layer, float value);

    public static native void setLayerTrRotation(long ptr, String layer, float value);

    public static native void setLayerTrOpacity(long ptr, String layer, float value);

    public static native void setLayerTrAnchor(long ptr, String layer, float x, float y);

    public static native void setLayerTrPosition(long ptr, String layer, float x, float y);

    public static native void setLayerTrScale(long ptr, String layer, float w, float h);

    public static native void setDynamicLayerColor(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerStrokeColor(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerFillOpacity(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerStrokeOpacity(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerStrokeWidth(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerTrRotation(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerTrOpacity(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerTrAnchor(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerTrPosition(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

    public static native void setDynamicLayerTrScale(long ptr, String layer, KLottieProperty.DynamicProperty<?> dynamicProperty);

}
