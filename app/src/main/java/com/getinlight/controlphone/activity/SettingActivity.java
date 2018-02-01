package com.getinlight.controlphone.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SettingItemView siv_update = findViewById(R.id.siv_update);

        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setChecked(open_update);

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !siv_update.isChecked();
                siv_update.setChecked(isChecked);
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, isChecked);
            }
        });

    }
}
