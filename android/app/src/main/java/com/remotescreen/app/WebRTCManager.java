package com.remotescreen.app;

import android.content.Context;
import android.content.Intent;

import org.webrtc.CapturerObserver;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class WebRTCManager {

    private final PeerConnectionFactory factory;
    private VideoSource videoSource;
    private VideoTrack videoTrack;
    private ScreenVideoCapturer screenCapturer;

    public WebRTCManager(Context context) {

        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions
                        .builder(context.getApplicationContext())
                        .createInitializationOptions()
        );

        factory = PeerConnectionFactory.builder()
                .createPeerConnectionFactory();
    }

    public PeerConnectionFactory getFactory() {
        return factory;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }

    public void createScreenTrack(
            Context context,
            Intent permissionData,
            int width,
            int height,
            int fps) {

        videoSource = factory.createVideoSource(false);

        CapturerObserver observer =
                videoSource.getCapturerObserver();

        screenCapturer = new ScreenVideoCapturer(
                context,
                permissionData
        );

        screenCapturer.start(
                context,
                observer,
                width,
                height,
                fps
        );

        videoTrack = factory.createVideoTrack(
                "screen-video",
                videoSource
        );
    }

    public void release() {

        if (screenCapturer != null) {
            screenCapturer.stop();
            screenCapturer = null;
        }

        if (videoTrack != null) {
            videoTrack.dispose();
            videoTrack = null;
        }

        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }

        factory.dispose();
    }
            }
