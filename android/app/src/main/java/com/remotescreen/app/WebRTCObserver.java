package com.remotescreen.app;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.VideoTrack;

public class WebRTCObserver implements PeerConnection.Observer {

    public interface Listener {
        void onIceCandidate(IceCandidate candidate);
        void onVideoTrack(VideoTrack videoTrack);
    }

    private final Listener listener;

    public WebRTCObserver(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
        if (listener != null) {
            listener.onIceCandidate(candidate);
        }
    }

    @Override
    public void onTrack(RtpTransceiver transceiver) {
        if (transceiver != null &&
                transceiver.getReceiver() != null) {

            RtpReceiver receiver =
                    transceiver.getReceiver();

            if (receiver.track() instanceof VideoTrack) {

                VideoTrack track =
                        (VideoTrack) receiver.track();

                if (listener != null) {
                    listener.onVideoTrack(track);
                }
            }
        }
    }

    @Override
    public void onAddStream(MediaStream stream) {
        if (stream != null &&
                !stream.videoTracks.isEmpty()) {

            if (listener != null) {
                listener.onVideoTrack(
                        stream.videoTracks.get(0)
                );
            }
        }
    }

    @Override
    public void onSignalingChange(
            PeerConnection.SignalingState state) {
    }

    @Override
    public void onIceConnectionChange(
            PeerConnection.IceConnectionState state) {
    }

    @Override
    public void onIceConnectionReceivingChange(
            boolean receiving) {
    }

    @Override
    public void onIceGatheringChange(
            PeerConnection.IceGatheringState state) {
    }

    @Override
    public void onRemoveStream(MediaStream stream) {
    }

    @Override
    public void onDataChannel(
            org.webrtc.DataChannel dataChannel) {
    }

    @Override
    public void onRenegotiationNeeded() {
    }

    @Override
    public void onAddTrack(
            RtpReceiver receiver,
            MediaStream[] mediaStreams) {
    }
}
