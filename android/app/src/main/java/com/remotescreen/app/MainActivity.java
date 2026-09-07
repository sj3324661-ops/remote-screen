package com.remotescreen.app;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    private TextView title;
    private TextView status;
    private EditText codeInput;
    private Button connectButton;

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

        controllerButton.setOnClickListener(v -> {
            title.setText("PHONE A — CONTROLLER");
            status.setText("Pair Code बनाने के लिए तैयार");

            codeInput.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);
        });

        screenButton.setOnClickListener(v -> {
            title.setText("PHONE B — SCREEN");
            status.setText("6 digit Pair Code डालें");

            codeInput.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.VISIBLE);
        });

        connectButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();

            if (code.length() != 6) {
                status.setText("कृपया 6 digit Pair Code डालें");
                return;
            }

            status.setText("Connecting...");
        });
    }

    private void requestScreenPermission() {
        MediaProjectionManager manager =
                (MediaProjectionManager) getSystemService(
                        MEDIA_PROJECTION_SERVICE);

        Intent captureIntent = manager.createScreenCaptureIntent();

        startActivityForResult(
                captureIntent,
                SCREEN_CAPTURE_REQUEST
        );
    }
}
