package com.remotescreen.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Remote Screen");
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);

        Button phoneA = new Button(this);
        phoneA.setText("PHONE A CONTROLLER");

        Button phoneB = new Button(this);
        phoneB.setText("PHONE B - SCREEN");

        status = new TextView(this);
        status.setText("ऊपर से एक विकल्प चुनें");
        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);
        status.setTextColor(Color.BLACK);

        phoneA.setOnClickListener(v -> {
            status.setText("PHONE A CONTROLLER चुना गया");
        });

        phoneB.setOnClickListener(v -> {
            status.setText("PHONE B - SCREEN चुना गया");
        });

        layout.addView(title);
        layout.addView(phoneA);
        layout.addView(phoneB);
        layout.addView(status);

        setContentView(layout);
    }
            }
