package com.getinlight.controlphone.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.utils.ToastUtil;
import com.getinlight.controlphone.view.SettingItemView;

public class Setup2Activity extends AppCompatActivity {

    private SettingItemView sivSimBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivSimBind = findViewById(R.id.siv_sim_bind);

        String simNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(simNumber)) {
            sivSimBind.setChecked(false);
        } else {
            sivSimBind.setChecked(true);
        }

        sivSimBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = !sivSimBind.isChecked();
                if (checked) {  //存储序列卡号
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.show(getApplicationContext(), "没有相关权限");
                        return;
                    }
                    SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, manager.getSimSerialNumber());
                } else {  //删除序列号
                    SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
                sivSimBind.setChecked(checked);
            }
        });
    }

    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);

        finish();
    }

    public void nextPage(View v) {
        if (sivSimBind.isChecked()) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.show(this, "请您绑定SIM卡");
        }

    }
}
