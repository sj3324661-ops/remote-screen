package com.remotescreen.app;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import io.socket.client.Socket;

public class MainActivity extends Activity {

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    private TextView title;
    private TextView status;
    private EditText codeInput;
    private Button connectButton;
    private LinearLayout controlPanel;
    private SurfaceViewRenderer remoteVideoView;

    private Socket socket;

    private WebRTCManager webRTCManager;
    private PeerConnectionManager peerConnectionManager;
    private WebRTCSignaling signaling;

    private EglBase eglBase;

    private boolean isScreenPhone = false;
    private boolean isPaired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        status = findViewById(R.id.status);
        codeInput = findViewById(R.id.codeInput);
        connectButton = findViewById(R.id.connectButton);
        controlPanel = findViewById(R.id.controlPanel);
        remoteVideoView = findViewById(R.id.remoteVideoView);

        eglBase = EglBase.create();

        remoteVideoView.init(
                eglBase.getEglBaseContext(),
                null
        );

        remoteVideoView.setEnableHardwareScaler(true);
        remoteVideoView.setMirror(false);

        Button controllerButton =
                findViewById(R.id.controllerButton);

        Button screenButton =
                findViewById(R.id.screenButton);

        socket = SocketManager.getSocket();

        webRTCManager =
                new WebRTCManager(this);

        signaling =
                new WebRTCSignaling(
                        socket,
                        new WebRTCSignaling.Listener() {

                            @Override
                            public void onOffer(
                                    SessionDescription offer) {

                                if (peerConnectionManager == null) {
                                    createPeerConnection();
                                }

                                peerConnectionManager
                                        .setRemoteDescription(
                                                offer
                                        );

                                createAnswer();
                            }

                            @Override
                            public void onAnswer(
                                    SessionDescription answer) {

                                if (peerConnectionManager != null) {

                                    peerConnectionManager
                                            .setRemoteDescription(
                                                    answer
                                            );
                                }
                            }

                            @Override
                            public void onIceCandidate(
                                    IceCandidate candidate) {

                                if (peerConnectionManager != null) {

                                    peerConnectionManager
                                            .addIceCandidate(
                                                    candidate
                                            );
                                }
                            }

                            @Override
                            public void onError(
                                    String message) {

                                runOnUiThread(() ->
                                        status.setText(
                                                "WebRTC Error: "
                                                        + message
                                        )
                                );
                            }
                        }
                );

        socket.on("room-created", args ->
                runOnUiThread(() -> {

                    if (args.length > 0) {

                        status.setText(
                                "Pair Code: "
                                        + String.valueOf(args[0])
                        );
                    }
                })
        );

        socket.on("joined-room", args ->
                runOnUiThread(() -> {

                    isPaired = true;

                    status.setText(
                            "Phone B connected"
                    );
                })
        );

        socket.on("peer-connected", args ->
                runOnUiThread(() -> {

                    isPaired = true;

                    status.setText(
                            "दूसरा Phone connected"
                    );

                    if (!isScreenPhone) {
                        createPeerConnection();
                    }
                })
        );

        socket.on("room-error", args ->
                runOnUiThread(() -> {

                    if (args.length > 0) {

                        status.setText(
                                String.valueOf(args[0])
                        );
                    }
                })
        );

        controllerButton.setOnClickListener(v -> {

            isScreenPhone = false;

            title.setText(
                    "PHONE A — CONTROLLER"
            );

            status.setText(
                    "Pair Code बनाया जा रहा है..."
            );

            codeInput.setVisibility(
                    View.GONE
            );

            connectButton.setVisibility(
                    View.GONE
            );

            remoteVideoView.setVisibility(
                    View.VISIBLE
            );

            SocketManager.connect();

            socket.emit("create-room");
        });

        screenButton.setOnClickListener(v -> {

            isScreenPhone = true;

            title.setText(
                    "PHONE B — SCREEN"
            );

            status.setText(
                    "6 digit Pair Code डालें"
            );

            codeInput.setVisibility(
                    View.VISIBLE
            );

            connectButton.setVisibility(
                    View.VISIBLE
            );

            remoteVideoView.setVisibility(
                    View.GONE
            );

            SocketManager.connect();
        });

        connectButton.setOnClickListener(v -> {

            String code =
                    codeInput.getText()
                            .toString()
                            .trim();

            if (code.length() != 6) {

                status.setText(
                        "कृपया 6 digit Pair Code डालें"
                );

                return;
            }

            status.setText(
                    "Phone B connect हो रहा है..."
            );

            socket.emit(
                    "join-room",
                    code
            );

            requestScreenPermission();
        });
    }

    private void createPeerConnection() {

        if (peerConnectionManager != null) {
            return;
        }

        peerConnectionManager =
                new PeerConnectionManager(
                        webRTCManager.getFactory()
                );

        WebRTCObserver observer =
                new WebRTCObserver(
                        new WebRTCObserver.Listener() {

                            @Override
                            public void onIceCandidate(
                                    IceCandidate candidate) {

                                signaling.sendIceCandidate(
                                        candidate
                                );
                            }

                            @Override
                            public void onVideoTrack(
                                    VideoTrack videoTrack) {

                                runOnUiThread(() -> {

                                    remoteVideoView
                                            .setVisibility(
                                                    View.VISIBLE
                                            );

                                    controlPanel
                                            .setVisibility(
                                                    View.GONE
                                            );

                                    status.setText(
                                            "LIVE SCREEN CONNECTED"
                                    );

                                    videoTrack.addSink(
                                            remoteVideoView
                                    );
                                });
                            }
                        }
                );

        peerConnectionManager
                .createPeerConnection(
                        observer
                );
    }

    private void createAnswer() {

        if (peerConnectionManager == null) {
            return;
        }

        peerConnectionManager.createAnswer(
                new SimpleSdpObserver() {

                    @Override
                    public void onCreateSuccess(
                            SessionDescription answer) {

                        peerConnectionManager
                                .setLocalDescription(
                                        answer,
                                        new SimpleSdpObserver() {

                                            @Override
                                            public void onSetSuccess() {

                                                signaling
                                                        .sendAnswer(
                                                                answer
                                                        );
                                            }
                                        }
                                );
                    }

                    @Override
                    public void onCreateFailure(
                            String error) {

                        runOnUiThread(() ->
                                status.setText(
                                        "Answer Error: "
                                                + error
                                )
                        );
                    }
                }
        );
    }

    private void requestScreenPermission() {

        MediaProjectionManager manager =
                (MediaProjectionManager)
                        getSystemService(
                                MEDIA_PROJECTION_SERVICE
                        );

        if (manager == null) {

            status.setText(
                    "Screen Capture उपलब्ध नहीं है"
            );

            return;
        }

        Intent captureIntent =
                manager.createScreenCaptureIntent();

        startActivityForResult(
                captureIntent,
                SCREEN_CAPTURE_REQUEST
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode !=
                SCREEN_CAPTURE_REQUEST) {
            return;
        }

        if (resultCode == RESULT_OK &&
                data != null) {

            status.setText(
                    "Screen sharing शुरू हो रही है..."
            );

            startScreenWebRTC(data);

        } else {

            status.setText(
                    "Screen sharing की permission नहीं मिली"
            );
        }
    }

    private void startScreenWebRTC(
            Intent permissionData) {

        createPeerConnection();

        webRTCManager.createScreenTrack(
                this,
                permissionData,
                720,
                1280,
                15
        );

        VideoTrack track =
                webRTCManager.getVideoTrack();

        if (track != null &&
                peerConnectionManager != null) {

            peerConnectionManager
                    .addVideoTrack(track);

            createOffer();
        }
    }

    private void createOffer() {

        if (peerConnectionManager == null) {
            return;
        }

        peerConnectionManager.createOffer(
                new SimpleSdpObserver() {

                    @Override
                    public void onCreateSuccess(
                            SessionDescription offer) {

                        peerConnectionManager
                                .setLocalDescription(
                                        offer,
                                        new SimpleSdpObserver() {

                                            @Override
                                            public void onSetSuccess() {

                                                signaling
                                                        .sendOffer(
                                                                offer
                                                        );
                                            }
                                        }
                                );
                    }

                    @Override
                    public void onCreateFailure(
                            String error) {

                        runOnUiThread(() ->
                                status.setText(
                                        "Offer Error: "
                                                + error
                                )
                        );
                    }
                }
        );
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
        public void onCreateFailure(
                String error) {
        }

        @Override
        public void onSetFailure(
                String error) {
        }
    }

    @Override
    protected void onDestroy() {

        if (remoteVideoView != null) {

            remoteVideoView.release();
        }

        if (signaling != null) {

            signaling.destroy();
        }

        if (peerConnectionManager != null) {

            peerConnectionManager.close();
        }

        if (webRTCManager != null) {

            webRTCManager.release();
        }

        if (eglBase != null) {

            eglBase.release();
        }

        super.onDestroy();
    }
    }
