package com.example.androidlottieapp;

import java.io.Serializable;

public class KLottieItem implements Serializable {

    private String path;
    private String name;
    private KLottieDrawable drawable = null;

    public KLottieItem(String path, String name, KLottieDrawable drawable) {
        this.path = path;
        this.name = name;
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return "KLottieItem{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KLottieDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(KLottieDrawable drawable) {
        this.drawable = drawable;
    }
}
