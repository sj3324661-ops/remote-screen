package com.remotescreen.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.socket.client.Socket;

public class MainActivity extends Activity {

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

        Button controllerButton = findViewById(R.id.controllerButton);
        Button screenButton = findViewById(R.id.screenButton);

        socket = SocketManager.getSocket();

        socket.on("room-created", args -> runOnUiThread(() -> {
            if (args.length > 0) {
                status.setText("Pair Code: " + args[0]);
            }
        }));

        socket.on("joined-room", args -> runOnUiThread(() -> {
            status.setText("Phone B connected");
        }));

        socket.on("peer-connected", args -> runOnUiThread(() -> {
            status.setText("दूसरा Phone connected");
        }));

        socket.on("room-error", args -> runOnUiThread(() -> {
            if (args.length > 0) {
                status.setText(String.valueOf(args[0]));
            }
        }));

        controllerButton.setOnClickListener(v -> {

            title.setText("PHONE A — CONTROLLER");
            status.setText("Connecting...");

            codeInput.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);

            SocketManager.connect();

            socket.emit("create-room");
        });

        screenButton.setOnClickListener(v -> {

            title.setText("PHONE B — SCREEN");
            status.setText("6 digit Pair Code डालें");

            codeInput.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.VISIBLE);

            SocketManager.connect();
        });

        connectButton.setOnClickListener(v -> {

            String code = codeInput.getText().toString().trim();

            if (code.length() != 6) {
                status.setText("कृपया 6 digit Pair Code डालें");
                return;
            }

            status.setText("Connecting...");

            socket.emit("join-room", code);
        });
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
