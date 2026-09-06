package com.remotescreen.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("Remote Screen");
        title.setTextSize(30);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);

        Button controller = new Button(this);
        controller.setText("Phone A — Controller");

        Button screen = new Button(this);
        screen.setText("Phone B — Screen");

        controller.setOnClickListener(v ->
            Toast.makeText(this, "Phone A चुना गया", Toast.LENGTH_SHORT).show()
        );

        screen.setOnClickListener(v ->
            Toast.makeText(this, "Phone B चुना गया", Toast.LENGTH_SHORT).show()
        );

        layout.addView(title);
        layout.addView(controller);
        layout.addView(screen);

        setContentView(layout);
    }
                           }
