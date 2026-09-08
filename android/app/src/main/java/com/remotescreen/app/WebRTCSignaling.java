package com.remotescreen.app;

import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import io.socket.client.Socket;

public class WebRTCSignaling {

    public interface Listener {
        void onOffer(SessionDescription offer);
        void onAnswer(SessionDescription answer);
        void onIceCandidate(IceCandidate candidate);
        void onError(String message);
    }

    private final Socket socket;
    private final Listener listener;

    public WebRTCSignaling(
            Socket socket,
            Listener listener) {

        this.socket = socket;
        this.listener = listener;

        setupListeners();
    }

    private void setupListeners() {

        socket.on("signal", args -> {

            if (args.length == 0) {
                return;
            }

            try {

                JSONObject data =
                        (JSONObject) args[0];

                String type =
                        data.optString("type");

                if ("offer".equals(type)) {

                    SessionDescription offer =
                            new SessionDescription(
                                    SessionDescription.Type.OFFER,
                                    data.optString("sdp")
                            );

                    listener.onOffer(offer);

                } else if ("answer".equals(type)) {

                    SessionDescription answer =
                            new SessionDescription(
                                    SessionDescription.Type.ANSWER,
                                    data.optString("sdp")
                            );

                    listener.onAnswer(answer);

                } else if ("ice".equals(type)) {

                    JSONObject candidateJson =
                            data.optJSONObject("candidate");

                    if (candidateJson != null) {

                        IceCandidate candidate =
                                new IceCandidate(
                                        candidateJson.optString(
                                                "sdpMid"
                                        ),
                                        candidateJson.optInt(
                                                "sdpMLineIndex"
                                        ),
                                        candidateJson.optString(
                                                "candidate"
                                        )
                                );

                        listener.onIceCandidate(
                                candidate
                        );
                    }
                }

            } catch (Exception e) {

                if (listener != null) {
                    listener.onError(
                            e.getMessage()
                    );
                }
            }
        });
    }

    public void sendOffer(
            SessionDescription description) {

        sendSessionDescription(
                "offer",
                description
        );
    }

    public void sendAnswer(
            SessionDescription description) {

        sendSessionDescription(
                "answer",
                description
        );
    }

    private void sendSessionDescription(
            String type,
            SessionDescription description) {

        try {

            JSONObject data =
                    new JSONObject();

            data.put(
                    "type",
                    type
            );

            data.put(
                    "sdp",
                    description.description
            );

            socket.emit(
                    "signal",
                    data
            );

        } catch (Exception e) {

            if (listener != null) {
                listener.onError(
                        e.getMessage()
                );
            }
        }
    }

    public void sendIceCandidate(
            IceCandidate candidate) {

        try {

            JSONObject candidateJson =
                    new JSONObject();

            candidateJson.put(
                    "sdpMid",
                    candidate.sdpMid
            );

            candidateJson.put(
                    "sdpMLineIndex",
                    candidate.sdpMLineIndex
            );

            candidateJson.put(
                    "candidate",
                    candidate.sdp
            );

            JSONObject data =
                    new JSONObject();

            data.put(
                    "type",
                    "ice"
            );

            data.put(
                    "candidate",
                    candidateJson
            );

            socket.emit(
                    "signal",
                    data
            );

        } catch (Exception e) {

            if (listener != null) {
                listener.onError(
                        e.getMessage()
                );
            }
        }
    }

    public void destroy() {
        socket.off("signal");
    }
    }
