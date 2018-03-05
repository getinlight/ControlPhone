package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.service.LockScreenService;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.ServiceStatusUtil;
import com.getinlight.controlphone.utils.SpUtil;

public class ProcessSettingActivity extends AppCompatActivity {

    private CheckBox cb_show_system;
    private CheckBox cb_lock_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initSystemShow();
        initLockScreenClear();
    }

    private void initLockScreenClear() {
        cb_lock_clear = findViewById(R.id.cb_lock_clear);

        boolean running = ServiceStatusUtil.isServiceRunning(this, "com.getinlight.controlphone.service.LockScreenService");
        cb_lock_clear.setChecked(running);
        if (running) {
            cb_lock_clear.setText("锁屏清理已开启");
        } else {
            cb_lock_clear.setText("锁屏清理已关闭");
        }

        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_lock_clear.setText("锁屏清理已开启");
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                } else {
                    cb_lock_clear.setText("锁屏清理已关闭");
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }

        });
    }

    private void initSystemShow() {
        cb_show_system = findViewById(R.id.cb_show_system);

        boolean isShow = SpUtil.getBoolean(this, ConstantValue.SHOW_SYSTEM, false);
        cb_show_system.setChecked(isShow);
        if (isShow) {
            cb_show_system.setText("显示系统进程");
        } else {
            cb_show_system.setText("隐藏系统进程");
        }

        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_show_system.setText("显示系统进程");
                } else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
            }

        });
    }
}
