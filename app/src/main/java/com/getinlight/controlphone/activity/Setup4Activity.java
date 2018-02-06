package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.utils.ToastUtil;

public class Setup4Activity extends AppCompatActivity {

    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        cb_box = findViewById(R.id.cb_box);
        cb_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, cb_box.isChecked());
            }
        });

        SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
    }

    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    public void nextPage(View v) {

        SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, cb_box.isChecked());
        if (cb_box.isChecked()) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(),"请开启手机防盗设置");
        }
    }
}
