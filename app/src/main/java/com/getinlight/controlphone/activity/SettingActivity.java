package com.getinlight.controlphone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.db.dao.BlackNumberDao;
import com.getinlight.controlphone.service.AddressService;
import com.getinlight.controlphone.service.AppLockService;
import com.getinlight.controlphone.service.BlackNumberService;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.ServiceStatusUtil;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.view.SettingClickView;
import com.getinlight.controlphone.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private String[] items = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};//归属地显示风格样式名称数组
    private int style;//缓存的样式
    private SettingClickView scv_address;
    private SettingClickView scv_address_location;
    private SettingItemView siv_blacknumber;
    private SettingItemView siv_app_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdateView();
        initAddressView();
        initAddressStyleView();
        initAddressLocation();
        initBlackNumber();
        initAppLock();

        style = SpUtil.getInt(this, ConstantValue.ADDRESS_STYLE, 0);

    }

    private void initAppLock() {
        siv_app_lock = findViewById(R.id.siv_app_lock);
        boolean isRunning = ServiceStatusUtil.isServiceRunning(this, "com.getinlight.controlphone.service.AppLockService");
        siv_app_lock.setChecked(isRunning);
        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = siv_app_lock.isChecked();
                siv_app_lock.setChecked(!checked);
                if (!checked) {
                    startService(new Intent(getApplicationContext(), AppLockService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AppLockService.class));
                }
            }
        });
    }

    private void initBlackNumber() {
        siv_blacknumber = findViewById(R.id.siv_blacknumber);
        boolean isRunning = ServiceStatusUtil.isServiceRunning(this, "com.getinlight.controlphone.service.BlackNumberService");
        siv_blacknumber.setChecked(isRunning);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = siv_blacknumber.isChecked();
                siv_blacknumber.setChecked(!checked);
                if (!checked) {
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });
    }

    private void initAddressLocation() {
        scv_address_location = findViewById(R.id.scv_address_location);
        scv_address_location.setTitle("归属地提示框位置");
        scv_address_location.setDesc("设置归属地提示框位置");
        scv_address_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    private void initAddressStyleView() {
        scv_address = findViewById(R.id.scv_address_style);
        scv_address.setTitle("归属地提示风格");
        scv_address.setDesc(items[style]);
        scv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });
    }

    private void showSingleChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.setup1);
        builder.setTitle("归属地提示风格");
        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_STYLE, which);
                scv_address.setDesc(items[which]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void initAddressView() {
        SettingItemView siv_address = findViewById(R.id.siv_address);
        boolean serviceRunning = ServiceStatusUtil.isServiceRunning(this, "com.getinlight.controlphone.service.AddressService");
        if (serviceRunning) {
            siv_address.setChecked(true);
        } else {
            siv_address.setChecked(false);
        }
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_address.isChecked()) {
                    siv_address.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                } else {
                    siv_address.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    private void initUpdateView() {
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
