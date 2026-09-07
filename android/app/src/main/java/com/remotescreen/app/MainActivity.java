package com.remotescreen.app;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView title = findViewById(R.id.title);
        TextView status = findViewById(R.id.status);
        Button controllerButton = findViewById(R.id.controllerButton);
        Button screenButton = findViewById(R.id.screenButton);

        controllerButton.setOnClickListener(v -> {
            title.setText("Phone A — Controller");
            status.setText("Controller mode selected");
        });

        screenButton.setOnClickListener(v -> {
            title.setText("Phone B — Screen");
            status.setText("Screen sharing permission required");

            MediaProjectionManager manager =
                    (MediaProjectionManager) getSystemService(
                            MEDIA_PROJECTION_SERVICE);

            Intent captureIntent = manager.createScreenCaptureIntent();

            startActivityForResult(
                    captureIntent,
                    SCREEN_CAPTURE_REQUEST
            );
        });
    }
}
