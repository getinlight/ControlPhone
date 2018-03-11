package com.getinlight.controlphone.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.db.dao.AppLockDao;
import com.getinlight.controlphone.domain.AppInfo;
import com.getinlight.controlphone.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {

    private Button btn_unlock;
    private Button btn_lock;
    private LinearLayout ll_unlock;
    private LinearLayout ll_lock;
    private TextView tv_unlock;
    private TextView tv_lock;
    private ListView lv_unlock;
    private ListView lv_lock;
    private List<AppInfo> mAppInfoList;
    private ArrayList<AppInfo> mLockList;
    private ArrayList<AppInfo> mUnlockList;
    private AppLockDao mLockDao;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initUI();
        initData();
    }

    private void initData() {

        new Thread(){
            @Override
            public void run() {
                //1.获取手机中的所有应用
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁和未加锁应用
                mLockList = new ArrayList<>();
                mUnlockList = new ArrayList<>();

                mLockDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mLockDao.findAll();
                for (AppInfo appInfo : mAppInfoList) {
                    if (lockPackageList.contains(appInfo.getPackageName())) {
                        mLockList.add(appInfo);
                    } else {
                        mUnlockList.add(appInfo);
                    }
                }
            }
        }.start();


    }

    private void initUI() {
        btn_unlock = findViewById(R.id.btn_unlock);
        btn_lock = findViewById(R.id.btn_lock);

        ll_unlock = findViewById(R.id.ll_unlock);
        ll_lock = findViewById(R.id.ll_lock);

        tv_unlock = findViewById(R.id.tv_unlock);
        tv_lock = findViewById(R.id.tv_lock);

        lv_unlock = findViewById(R.id.lv_unlock);
        lv_lock = findViewById(R.id.lv_lock);
    }
}
