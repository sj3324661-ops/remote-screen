package com.remotescreen.app;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 50, 30, 30);

        TextView title = new TextView(this);
        title.setText("Remote Screen");
        title.setTextSize(28);

        Button phoneA = new Button(this);
        phoneA.setText("PHONE A CONTROLLER");

        Button phoneB = new Button(this);
        phoneB.setText("PHONE B - SCREEN");

        layout.addView(title);
        layout.addView(phoneA);
        layout.addView(phoneB);

        phoneA.setOnClickListener(v -> {
            title.setText("Phone A Controller");
        });

        phoneB.setOnClickListener(v -> {
            title.setText("Screen share permission required");

            MediaProjectionManager manager =
                    (MediaProjectionManager) getSystemService(
                            MEDIA_PROJECTION_SERVICE
                    );

            Intent captureIntent = manager.createScreenCaptureIntent();
            startActivityForResult(
                    captureIntent,
                    SCREEN_CAPTURE_REQUEST
            );
        });

        setContentView(layout);
    }
}
