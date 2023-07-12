package com.example.androidlottieapp;

import android.content.Context;

import java.io.InputStream;


public class KFileReader {

    private static final ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    private static final ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();

    public static String fromAssets(Context context, String asset) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(asset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readStream(inputStream);
    }

    private static String readStream(InputStream inputStream) {
        if (inputStream == null) return null;

        int totalRead = 0;
        byte[] readBuffer = readBufferLocal.get();
        if (readBuffer == null) {
            readBuffer = new byte[64 * 1024];
            readBufferLocal.set(readBuffer);
        }
        try {
            int readLen;
            byte[] buffer = bufferLocal.get();
            if (buffer == null) {
                buffer = new byte[4096];
                bufferLocal.set(buffer);
            }
            while ((readLen = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                if (readBuffer.length < totalRead + readLen) {
                    byte[] newBuffer = new byte[readBuffer.length * 2];
                    System.arraycopy(readBuffer, 0, newBuffer, 0, totalRead);
                    readBuffer = newBuffer;
                    readBufferLocal.set(readBuffer);
                }
                if (readLen > 0) {
                    System.arraycopy(buffer, 0, readBuffer, totalRead, readLen);
                    totalRead += readLen;
                }
            }
        } catch (Throwable e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Throwable ignore) {
            }
        }

        return new String(readBuffer, 0, totalRead);
    }
}
