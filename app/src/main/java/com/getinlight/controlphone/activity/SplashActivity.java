package com.getinlight.controlphone.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.getinlight.controlphone.R;

import org.json.JSONArray;

public class SplashActivity extends Activity {

    private TextView tv_version_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除activity头第一种方式
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        initUI();
        initData();
    }

    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            tv_version_name.setText("版本名称: "+packageInfo.versionName);
            //监测更新
            int mVersionCode = packageInfo.versionCode;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
    }
}
