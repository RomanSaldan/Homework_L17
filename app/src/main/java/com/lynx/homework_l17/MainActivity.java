package com.lynx.homework_l17;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView tvText_AM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText_AM = (TextView) findViewById(R.id.tvText_AM);
        tvText_AM.setText("This string was changed. And commit was done from git terminal");
    }

}
