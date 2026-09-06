package com.remotescreen.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("Remote Screen\n\nApp तैयार है.");
        text.setTextSize(22);
        text.setPadding(40, 80, 40, 40);

        setContentView(text);
    }
}
