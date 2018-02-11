package com.getinlight.controlphone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.service.AddressService;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.ServiceStatusUtil;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.view.SettingClickView;
import com.getinlight.controlphone.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private String[] items = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};//归属地显示风格样式名称数组
    private int style;//缓存的样式
    private SettingClickView scv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdateView();
        initAddressView();
        initAddressStyleView();

        style = SpUtil.getInt(this, ConstantValue.ADDRESS_STYLE, 0);

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
        builder.setIcon(R.drawable.ic_launcher_background);
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
