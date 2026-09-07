package com.remotescreen.app;

import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.IceCandidate;

import java.util.ArrayList;
import java.util.List;

public class PeerConnectionManager {

    private final PeerConnectionFactory factory;
    private PeerConnection peerConnection;

    public PeerConnectionManager(PeerConnectionFactory factory) {
        this.factory = factory;
    }

    public void createPeerConnection(
            PeerConnection.Observer observer) {

        List<PeerConnection.IceServer> iceServers =
                new ArrayList<>();

        iceServers.add(
                PeerConnection.IceServer
                        .builder("stun:stun.l.google.com:19302")
                        .createIceServer()
        );

        PeerConnection.RTCConfiguration config =
                new PeerConnection.RTCConfiguration(
                        iceServers
                );

        peerConnection =
                factory.createPeerConnection(
                        config,
                        observer
                );
    }

    public PeerConnection getPeerConnection() {
        return peerConnection;
    }

    public void addIceCandidate(IceCandidate candidate) {

        if (peerConnection != null) {
            peerConnection.addIceCandidate(candidate);
        }
    }

    public void setRemoteDescription(
            SessionDescription description) {

        if (peerConnection != null) {
            peerConnection.setRemoteDescription(
                    new SimpleSdpObserver(),
                    description
            );
        }
    }

    public void close() {

        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
    }

    private static class SimpleSdpObserver
            implements PeerConnection.SdpObserver {

        @Override
        public void onCreateSuccess(
                SessionDescription description) {
        }

        @Override
        public void onSetSuccess() {
        }

        @Override
        public void onCreateFailure(String error) {
        }

        @Override
        public void onSetFailure(String error) {
        }
    }
  }
