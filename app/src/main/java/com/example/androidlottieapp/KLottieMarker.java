package com.example.androidlottieapp;

public class KLottieMarker {

    private String marker = "";
    private int inFrame = -1;
    private int outFrame = -1;

    KLottieMarker(String[] data) {
        try {
            if (data != null) {
                marker = data[0];
                inFrame = Integer.parseInt(data[1]);
                outFrame = Integer.parseInt(data[2]);
            }
        } catch (Exception ignore) {
        }
    }

    public KLottieMarker(String marker, int inFrame, int outFrame) {
        this.marker = marker;
        this.inFrame = inFrame;
        this.outFrame = outFrame;
    }

    public int getInFrame() {
        return inFrame;
    }

    public int getOutFrame() {
        return outFrame;
    }

    public String getMarker() {
        return marker;
    }

    @Override
    public String toString() {
        return "KLottieMarker{" +
                "marker='" + marker + '\'' +
                ", inFrame=" + inFrame +
                ", outFrame=" + outFrame +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KLottieMarker markerInfo = (KLottieMarker) o;

        if (inFrame != markerInfo.inFrame) return false;
        if (outFrame != markerInfo.outFrame) return false;
        if (marker == null) return markerInfo.marker == null;
        return marker.equals(markerInfo.marker);
    }

    @Override
    public int hashCode() {
        int result = marker.hashCode();
        result = 31 * result + inFrame;
        result = 31 * result + outFrame;
        return result;
    }
}
