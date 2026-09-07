package com.remotescreen.app;

import android.content.Context;
import android.content.Intent;

import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;

public class ScreenVideoCapturer {

    private final VideoCapturer capturer;
    private final SurfaceTextureHelper surfaceTextureHelper;

    public ScreenVideoCapturer(
            Context context,
            Intent permissionData) {

        EglBase eglBase = EglBase.create();

        surfaceTextureHelper =
                SurfaceTextureHelper.create(
                        "ScreenCaptureThread",
                        eglBase.getEglBaseContext()
                );

        capturer =
                new ScreenCapturerAndroid(
                        permissionData,
                        new android.media.projection.MediaProjection.Callback() {
                            @Override
                            public void onStop() {
                            }
                        }
                );
    }

    public void start(
            Context context,
            CapturerObserver observer,
            int width,
            int height,
            int fps) {

        capturer.initialize(
                surfaceTextureHelper,
                context,
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
}
