package com.example.androidlottieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class KLottieImageView extends AppCompatImageView {


    private KLottieDrawable drawable;
    private final int autoRepeat = KLottieOptions.DEFAULT;
    private boolean isAttachingWindow;
    private boolean isPlaying;

    public KLottieImageView(@NonNull Context context) {
        super(context);
    }

    public KLottieImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KLottieImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRepeat(boolean enabled) {
        if (drawable != null) {
            drawable.setRepeat(enabled);
        }
    }

    public void changeRepeatMode() {
        if (drawable != null) {
            drawable.changeRepeatMode();
        }
    }

    public void setLayerProperty(String layer, KLottieProperty property) {
        if (drawable != null) {
            drawable.setLayerProperty(layer, property);
        }
    }

    public boolean setLottieDrawable(KLottieDrawable lottieDrawable) {
        if (drawable != null && drawable.equals(lottieDrawable)) return false;
        setImageDrawable(lottieDrawable);
        return true;
    }

    public int getCustomStartFrame() {
        if (drawable == null) return -1;

        return drawable.getCustomStartFrame();
    }

    public void setCustomStartFrame(int frame) {
        if (drawable == null) return;

        drawable.setCustomStartFrame(frame);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable mDrawable) {
        if (mDrawable instanceof KLottieDrawable) {
            drawable = (KLottieDrawable) mDrawable;
            drawable.setAllowDecodeSingleFrame(true);
            isPlaying = drawable.isRunning();
        }
        super.setImageDrawable(mDrawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachingWindow = true;
        if (drawable != null) {
            drawable.setCallback(this);
            if (isPlaying) {
                drawable.start();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachingWindow = false;
        if (drawable != null) {
            drawable.stop();
        }
    }

    public void startLottie() {
        isPlaying = true;
        if (isAttachingWindow && drawable != null) {
            drawable.start();
        }
    }

    public KLottieDrawable getLottieDrawable() {
        return drawable;
    }
}
