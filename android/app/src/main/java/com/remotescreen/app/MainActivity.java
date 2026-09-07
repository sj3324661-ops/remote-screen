package com.remotescreen.app;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.socket.client.Socket;

public class MainActivity extends Activity {

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    private TextView title;
    private TextView status;
    private EditText codeInput;
    private Button connectButton;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        status = findViewById(R.id.status);
        codeInput = findViewById(R.id.codeInput);
        connectButton = findViewById(R.id.connectButton);

        Button controllerButton =
                findViewById(R.id.controllerButton);

        Button screenButton =
                findViewById(R.id.screenButton);

        socket = SocketManager.getSocket();

        socket.on("room-created", args ->
                runOnUiThread(() -> {

                    if (args.length > 0) {
                        status.setText(
                                "Pair Code: " + args[0]
                        );
                    }
                })
        );

        socket.on("joined-room", args ->
                runOnUiThread(() ->
                        status.setText(
                                "Phone B connected"
                        )
                )
        );

        socket.on("peer-connected", args ->
                runOnUiThread(() ->
                        status.setText(
                                "दूसरा Phone connected"
                        )
                )
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

        // PHONE A
        controllerButton.setOnClickListener(v -> {

            title.setText(
                    "PHONE A — CONTROLLER"
            );

            status.setText(
                    "Connecting..."
            );

            codeInput.setVisibility(
                    View.GONE
            );

            connectButton.setVisibility(
                    View.GONE
            );

            SocketManager.connect();

            socket.emit("create-room");
        });

        // PHONE B
        screenButton.setOnClickListener(v -> {

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

            SocketManager.connect();
        });

        // CONNECT PHONE B
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
                    "Connecting..."
            );

            socket.emit(
                    "join-room",
                    code
            );

            // Screen capture permission
            requestScreenPermission();
        });
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

        if (requestCode ==
                SCREEN_CAPTURE_REQUEST) {

            if (resultCode == RESULT_OK &&
                    data != null) {

                status.setText(
                        "Screen permission मिल गई"
                );

                // अगले step में इसी permission
                // से actual screen streaming शुरू करेंगे।

            } else {

                status.setText(
                        "Screen sharing की permission नहीं मिली"
                );
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (socket != null) {

            socket.off("room-created");
            socket.off("joined-room");
            socket.off("peer-connected");
            socket.off("room-error");
        }
    }
                }
