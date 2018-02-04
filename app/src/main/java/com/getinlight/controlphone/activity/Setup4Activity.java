package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

public class Setup4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);

        finish();
    }

    public void nextPage(View v) {
        SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);

        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
        startActivity(intent);

        finish();
    }
}
