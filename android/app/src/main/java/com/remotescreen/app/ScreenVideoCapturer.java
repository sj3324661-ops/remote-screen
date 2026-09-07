package com.remotescreen.app;

import android.content.Context;
import android.content.Intent;

import org.webrtc.CapturerObserver;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;

public class ScreenVideoCapturer {

    private final VideoCapturer capturer;
    private final SurfaceTextureHelper surfaceTextureHelper;

    public ScreenVideoCapturer(
            Context context,
            Intent permissionData,
            CapturerObserver observer) {

        surfaceTextureHelper =
                SurfaceTextureHelper.create(
                        "ScreenCaptureThread",
                        null
                );

        capturer =
                new ScreenCapturerAndroid(
                        permissionData,
                        new MediaProjectionCallback()
                );
    }

    public VideoCapturer getCapturer() {
        return capturer;
    }

    public SurfaceTextureHelper getSurfaceTextureHelper() {
        return surfaceTextureHelper;
    }

    public void start(
            int width,
            int height,
            int fps,
            CapturerObserver observer) {

        capturer.initialize(
                surfaceTextureHelper,
                null,
                observer
        );

        capturer.startCapture(
                width,
                height,
                fps
        );
    }

    public void stop() {

        try {
            capturer.stopCapture();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        capturer.dispose();
        surfaceTextureHelper.dispose();
    }

    private static class MediaProjectionCallback
            implements android.media.projection.MediaProjection.Callback {

        @Override
        public void onStop() {
            // User ने screen sharing बंद की
        }
    }
}
