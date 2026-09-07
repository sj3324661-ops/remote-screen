package com.remotescreen.app;

import android.content.Context;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class WebRTCManager {

    private final PeerConnectionFactory factory;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack videoTrack;

    public WebRTCManager(Context context) {

        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions
                        .builder(context)
                        .createInitializationOptions()
        );

        factory = PeerConnectionFactory.builder().createPeerConnectionFactory();
    }

    public PeerConnectionFactory getFactory() {
        return factory;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }

    public void createCameraTrack(Context context) {

        CameraEnumerator enumerator =
                new Camera2Enumerator(context);

        String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {

            if (enumerator.isFrontFacing(deviceName)) {

                videoCapturer =
                        enumerator.createCapturer(
                                deviceName,
                                null
                        );

                if (videoCapturer != null) {
                    break;
                }
            }
        }

        if (videoCapturer == null) {
            for (String deviceName : deviceNames) {

                videoCapturer =
                        enumerator.createCapturer(
                                deviceName,
                                null
                        );

                if (videoCapturer != null) {
                    break;
                }
            }
        }

        if (videoCapturer == null) {
            return;
        }

        videoSource =
                factory.createVideoSource(false);

        videoTrack =
                factory.createVideoTrack(
                        "screen-video",
                        videoSource
                );
    }

    public void release() {

        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (Exception ignored) {
            }

            videoCapturer.dispose();
        }

        if (videoSource != null) {
            videoSource.dispose();
        }

        if (videoTrack != null) {
            videoTrack.dispose();
        }

        factory.dispose();
    }
                          }
