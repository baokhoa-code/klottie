package com.example.androidlottieapp;

import static com.example.androidlottieapp.KLottieNative.createInfoByJson;
import static com.example.androidlottieapp.KLottieNative.getFrame;
import static com.example.androidlottieapp.KLottieNative.releaseInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KLottieDrawable extends BitmapDrawable implements Animatable {

    private static final String TAG = KLottieDrawable.class.getSimpleName();

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final ThreadPoolExecutor loadFrameRunnableQueue =
            new ThreadPoolExecutor(0, 4,
                    30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static ThreadPoolExecutor lottieCacheGenerateQueue;
    private final ArrayList<KLottieProperty.PropertyUpdate> pendingPropertyUpdates = new ArrayList<>();
    private final int[] metaData = new int[3];
    private ArrayList<KLottieProperty.PropertyUpdate> newPropertyUpdates = new ArrayList<>();
    private Runnable cacheGenerateTask;
    private Runnable loadFrameTask;
    private KLottieMarker selectedMarker = null;
    private volatile Bitmap renderingBitmap;
    private volatile Bitmap nextRenderingBitmap;
    private volatile Bitmap backgroundBitmap;
    private int customStartFrame = -1;
    private int customEndFrame = -1;

    private final int width;
    private final int height;
    private float speed = 1f;
    private int timeBetweenFrames;
    private int autoRepeat = KLottieOptions.REPEAT_MODE_INFINITE;
    private long lastFrameTime;
    private volatile boolean nextFrameIsLast;

    private boolean destroyWhenDone;
    private boolean decodeSingleFrame;
    private boolean singleFrameDecoded;
    private boolean forceFrameRedraw;
    private int currentFrame;
    private boolean isFPSLimit;
    private boolean isUserDrag = false;
    private boolean isReverseing = false;
    private boolean applyTransformation;
    private volatile boolean isRunning;
    private volatile boolean isRecycled;
    private volatile long nativePtr;
    private final String cacheName;
    private KLottieDrawableListener mListener = null;

    public KLottieDrawable(KLottieOptions options, KLottieDrawableListener listener) {
        this.width = options.w == KLottieOptions.DEFAULT ? 200 : options.w;
        this.height = options.h == KLottieOptions.DEFAULT ? 200 : options.h;
        this.cacheName = options.cacheName;
        this.mListener = listener;

        getPaint().setFlags(Paint.FILTER_BITMAP_FLAG);

        if (options.type == KLottieOptions.BuilderType.JSON) {
            initFromJson(options.json, options.startDecode);
        }

        if (options.autoRepeat != KLottieOptions.DEFAULT)
            setRepeat(options.autoRepeat);

        if (options.speed > 0) {
            setSpeed(options.speed);
        }

        if (options.properties != null) {
            if (newPropertyUpdates == null) newPropertyUpdates = new ArrayList<>();
            newPropertyUpdates.addAll(options.properties);
        }

        if (options.selectedMarker != null)
            setSelectMarker(options.selectedMarker);

    }

    /*************************************
     *************************************

     Handle
     *************************************
     ************************************/

    public static KLottieOptions fromAssets(@NonNull Context context,
                                            @NonNull String fileName, @NonNull String cacheName) {
        return new KLottieOptions(KFileReader.fromAssets(context, fileName), cacheName);
    }


    /*************************************
     *************************************

     Get, Set
     *************************************
     ************************************/

    public int getCurrentFrame() {
        return currentFrame;
    }

    public boolean isReverseing() {
        return isReverseing;
    }

    public void setReverseing(boolean reverseing) {
        isReverseing = reverseing;
    }

    public boolean isUserDrag() {
        return isUserDrag;
    }

    public void setUserDrag(boolean userDrag) {
        isUserDrag = userDrag;
    }

    /*************************************
     *************************************

     Runnable
     *************************************
     ************************************/

    private final Runnable uiRunnable = new Runnable() {
        @Override
        public void run() {
            singleFrameDecoded = true;
            invalidateSelf();
            decodeFrameFinishedInternal();
        }
    };

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    private final Runnable uiRunnableNoFrame = new Runnable() {
        @Override
        public void run() {
            loadFrameTask = null;
            decodeFrameFinishedInternal();
        }
    };

    public void rePlay() {
        currentFrame = getStartFrame();
    }

    private final Runnable loadFrameRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecycled) {
                return;
            }
            if (nativePtr == 0) {
                uiHandler.post(uiRunnableNoFrame);
                return;
            }
            if (backgroundBitmap == null) {
                try {
                    backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (backgroundBitmap != null) {
                if (!pendingPropertyUpdates.isEmpty()) {
                    for (KLottieProperty.PropertyUpdate entry : pendingPropertyUpdates) {
                        entry.apply(nativePtr);
                    }
                    pendingPropertyUpdates.clear();
                }
                try {
                    long ptrToUse = nativePtr;

                    int result = getFrame(ptrToUse, currentFrame, backgroundBitmap, width, height, backgroundBitmap.getRowBytes());
                    if (result == -1) {
                        uiHandler.post(uiRunnableNoFrame);
                        return;
                    }
                    nextRenderingBitmap = backgroundBitmap;
                    int framesPerUpdates = getFramesPerUpdate();
                    int endFrame = getEndFrame();
                    int startFrame = getStartFrame();

                    if (mListener != null)
                        mListener.onProgress(currentFrame);

                    if (!isReverseing) {
                        if (currentFrame + framesPerUpdates < endFrame) {
                            currentFrame += framesPerUpdates;
                            nextFrameIsLast = false;
                        } else if (autoRepeat == KLottieOptions.REPEAT_MODE_NONE && currentFrame + framesPerUpdates >= endFrame) {
                            if (mListener != null)
                                mListener.onStop();
                            nextFrameIsLast = true;
                        } else if (autoRepeat == KLottieOptions.REPEAT_MODE_INFINITE && currentFrame + framesPerUpdates >= endFrame) {
                            currentFrame = startFrame;
                            nextFrameIsLast = false;
                        }
                    } else {
                        if (currentFrame - framesPerUpdates > endFrame) {
                            currentFrame -= framesPerUpdates;
                            nextFrameIsLast = false;
                        } else if (autoRepeat == KLottieOptions.REPEAT_MODE_NONE && currentFrame - framesPerUpdates <= endFrame) {
                            if (mListener != null)
                                mListener.onStop();
                            nextFrameIsLast = true;
                        } else if (autoRepeat == KLottieOptions.REPEAT_MODE_INFINITE && currentFrame - framesPerUpdates <= endFrame) {
                            currentFrame = startFrame;
                            nextFrameIsLast = false;
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            uiHandler.post(uiRunnable);
        }
    };

    public String getCacheName() {
        return cacheName;
    }

    protected int getFramesPerUpdate() {
        return isFPSLimit ? 2 : 1;
    }

    public @Nullable
    KLottieMarker getSelectedMarker() {
        return selectedMarker;
    }

    public long getNativePtr() {
        return nativePtr;
    }

    public int getLayersCount() {
        return KLottieNative.getLayersCount(nativePtr);
    }

    public KLottieLayer getLayerInfo(int index) {
        return new KLottieLayer(KLottieNative.getLayerData(nativePtr, index));
    }

    public List<KLottieLayer> getLayers() {
        List<KLottieLayer> layers = new ArrayList<>();
        int max = getLayersCount();
        Log.i("Layers", "Number of layer: " + max);

        for (int i = 0; i < max; i++) {
            layers.add(getLayerInfo(i));
        }
        return layers;
    }

    public int getMarkersCount() {
        return KLottieNative.getMarkersCount(nativePtr);
    }

    public KLottieMarker getMarker(int index) {
        return new KLottieMarker(KLottieNative.getMarkerData(nativePtr, index));
    }

    public List<KLottieMarker> getMarkers() {
        List<KLottieMarker> markers = new ArrayList<>();
        int max = getMarkersCount();
        for (int i = 0; i < max; i++) {
            markers.add(getMarker(i));
        }
        return markers;
    }

    public boolean isRepeating() {
        return autoRepeat == KLottieOptions.REPEAT_MODE_INFINITE;
    }

    public int getCustomStartFrame() {
        return customStartFrame;
    }

    public void setCustomStartFrame(int frame) {
        if (frame > metaData[0]) {
            return;
        }
        customStartFrame = Math.max(frame, 0);
        if (frame == -1) customStartFrame = -1;
    }

    public int getCustomEndFrame() {
        return customEndFrame;
    }

    public void setCustomEndFrame(int frame) {
        if (frame > metaData[0]) {
            return;
        }
        customEndFrame = Math.max(frame, 0);
        if (frame == -1) customEndFrame = -1;
    }

    public void setSelectMarker(@Nullable KLottieMarker marker) {
        this.selectedMarker = marker;
    }

    public void setSpeed(float speed) {
        if (speed <= 0) return;
        this.speed = speed;
    }

    public void setRepeat(int repeatCount) {
        autoRepeat = repeatCount;
    }

    public void setRepeat(boolean enabled) {
        setRepeat(enabled ? KLottieOptions.REPEAT_MODE_INFINITE : KLottieOptions.REPEAT_MODE_NONE);
    }

    public void changeRepeatMode() {
        if (autoRepeat == KLottieOptions.REPEAT_MODE_INFINITE)
            setRepeat(KLottieOptions.REPEAT_MODE_NONE);
        else
            setRepeat(KLottieOptions.REPEAT_MODE_INFINITE);
        this.start();
    }

    public void setLayerProperty(String keyPath, KLottieProperty property) {
        newPropertyUpdates.add(new KLottieProperty.PropertyUpdate(property, keyPath));
        requestRedraw();
    }

    void setLayerProperties(List<KLottieProperty.PropertyUpdate> list) {
        newPropertyUpdates.addAll(list);
        requestRedraw();
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        decodeSingleFrame = value;
        if (decodeSingleFrame) {
            scheduleNextGetFrame();
        }
    }

    private void initFromJson(String json, boolean startDecode) {

        nativePtr = createInfoByJson(json, getCacheName(), metaData);

        isFPSLimit = metaData[1] >= 60;

        timeBetweenFrames = Math.max(isFPSLimit ? 32 : 16, (int) (1000.0f / metaData[1]));


        if (startDecode) {
            setAllowDecodeSingleFrame(true);
        }
    }

    private void setCurrentFrame(long now, long timeDiff, long timeCheck, boolean force) {
        backgroundBitmap = renderingBitmap;
        renderingBitmap = nextRenderingBitmap;
        nextRenderingBitmap = null;
        if (nextFrameIsLast) {
            stop();
        }
        loadFrameTask = null;

        singleFrameDecoded = true;
        if (KLottie.getScreenRefreshRate() <= 60) {
            lastFrameTime = now;
        } else {
            lastFrameTime = now - Math.min(16, timeDiff - timeCheck);
        }
        if (force && forceFrameRedraw) {
            singleFrameDecoded = false;
            forceFrameRedraw = false;
        }
        scheduleNextGetFrame();
    }

    private void requestRedraw() {
        if (!isRunning && decodeSingleFrame) {
            if (currentFrame <= getStartFrame() + 2) {
                currentFrame = getStartFrame();
            }
            nextFrameIsLast = false;
            singleFrameDecoded = false;
            if (!scheduleNextGetFrame()) {
                forceFrameRedraw = true;
            }
        }
        invalidateSelf();
    }

    private boolean scheduleNextGetFrame() {
        if (nativePtr == 0) return false;
        if (loadFrameTask != null || nextRenderingBitmap != null || destroyWhenDone
                || !isRunning && (!decodeSingleFrame || singleFrameDecoded)) {
            return false;
        }
        if (!newPropertyUpdates.isEmpty()) {
            pendingPropertyUpdates.addAll(newPropertyUpdates);
            newPropertyUpdates.clear();
        }
        loadFrameRunnableQueue.execute(loadFrameTask = loadFrameRunnable);
        return true;
    }

    private void decodeFrameFinishedInternal() {

        if (destroyWhenDone) {
            releaseRunningTasks();
            releaseInfo(nativePtr);
            nativePtr = 0;
        }

        if (nativePtr == 0) {
            recycleBitmaps();
            return;
        }

        scheduleNextGetFrame();

    }

    private void releaseRunningTasks() {
        if (cacheGenerateTask != null) {
            if (lottieCacheGenerateQueue.remove(cacheGenerateTask)) {
                cacheGenerateTask = null;
            }
        }
        if (nextRenderingBitmap != null && loadFrameTask != null) {
            loadFrameTask = null;
            nextRenderingBitmap = null;
        }
    }

    private void recycleBitmaps() {
        if (renderingBitmap != null) {
            renderingBitmap.recycle();
            renderingBitmap = null;
        }
        if (backgroundBitmap != null) {
            backgroundBitmap.recycle();
            backgroundBitmap = null;
        }
        if (nextRenderingBitmap != null) {
            nextRenderingBitmap.recycle();
            nextRenderingBitmap = null;
        }
    }

    public void recycle() {

        isRunning = false;
        isRecycled = true;
        releaseRunningTasks();

        if (loadFrameTask == null && cacheGenerateTask == null) {
            if (nativePtr != 0) {
                releaseInfo(nativePtr);
                nativePtr = 0;
            }
            recycleBitmaps();
        } else {
            destroyWhenDone = true;
        }
    }

    public int getRealEnd() {
        return metaData[0];
    }

    public int getEndFrame() {
        if (!isReverseing) {
            if (getSelectedMarker() != null && getSelectedMarker().getOutFrame() > 0)
                return Math.min(getSelectedMarker().getOutFrame(), metaData[0]);

            if (customEndFrame == -1) return metaData[0];
            else return Math.min(customEndFrame, metaData[0]);
        } else {
            if (getSelectedMarker() != null && getSelectedMarker().getInFrame() >= 0)
                return Math.min(getSelectedMarker().getInFrame(), metaData[0]);

            return Math.max(customStartFrame, 0);
        }
    }

    public int getStartFrame() {
        if (!isReverseing) {
            if (getSelectedMarker() != null && getSelectedMarker().getInFrame() >= 0)
                return Math.min(getSelectedMarker().getInFrame(), metaData[0]);

            return Math.max(customStartFrame, 0);
        } else {
            if (getSelectedMarker() != null && getSelectedMarker().getOutFrame() > 0)
                return Math.min(getSelectedMarker().getOutFrame(), metaData[0]);

            if (customEndFrame == -1) return metaData[0];
            else return Math.min(customEndFrame, metaData[0]);
        }
    }

    protected int findTimeBetweenFrames() {
        return !isFPSLimit ? (int) (timeBetweenFrames / speed) : (int) (timeBetweenFrames / (speed * 0.9));
    }

    /*************************************
     *************************************

     Override methods
     *************************************
     ************************************/

    @Override
    protected void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void start() {
        if (isRunning) return;
        isRunning = true;
        scheduleNextGetFrame();
        invalidateSelf();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        applyTransformation = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (nativePtr == 0 || destroyWhenDone) {
            return;
        }
        long now = SystemClock.elapsedRealtime();
        long timeDiff = Math.abs(now - lastFrameTime);
        int timeCheck;
        if (KLottie.getScreenRefreshRate() <= 60) {
            timeCheck = findTimeBetweenFrames() - 6;
        } else {
            timeCheck = findTimeBetweenFrames();
        }

        if (isRunning) {
            if (renderingBitmap == null && nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (nextRenderingBitmap != null &&
                    (renderingBitmap == null || timeDiff >= timeCheck)) {
                //update
                setCurrentFrame(now, timeDiff, timeCheck, false);
            }
        } else if ((forceFrameRedraw || decodeSingleFrame && timeDiff >= timeCheck) && nextRenderingBitmap != null) {
            setCurrentFrame(now, timeDiff, timeCheck, true);
        }
        if (renderingBitmap != null) {
            canvas.drawBitmap(renderingBitmap, 0, 0, getPaint());
            if (isRunning)
                invalidateSelf();
        }
        if (isUserDrag) this.stop();
    }

    @Override
    public int getMinimumHeight() {
        return height;
    }

    @Override
    public int getMinimumWidth() {
        return width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KLottieDrawable)) return false;

        KLottieDrawable that = (KLottieDrawable) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (getEndFrame() != that.getEndFrame()) return false;
        if (getStartFrame() != that.getStartFrame()) return false;
        if (autoRepeat != that.autoRepeat) return false;
        return cacheName.equals(that.cacheName);
    }

    public interface KLottieDrawableListener {
        void onStart();

        void onStop();

        void onProgress(int frame);
    }


}
