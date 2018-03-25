package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.engine.VirusDao;
import com.getinlight.controlphone.utils.MD5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntiVirusActivity extends AppCompatActivity {

    private static final int SCANNING = 101;
    private static final int SCANFINISH = 102;
    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pb_bar;
    private LinearLayout ll_add_text;
    private RotateAnimation rotateAnimation;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_name.setText(scanInfo.name);
                    TextView textView = new TextView(getApplicationContext());
                    if (scanInfo.isVirtus) {
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒: "+scanInfo.name);
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全: "+scanInfo.name);
                    }
                    ll_add_text.addView(textView, 0);
                    break;
                case SCANFINISH:
                    tv_name.setText("扫描完成");
                    iv_scanning.clearAnimation();
                    uninstallVirtus();
                    break;
                default:
                    break;
            }
        }
    };
    private ArrayList<ScanInfo> virtusScanInfos;

    private void uninstallVirtus() {
        for (ScanInfo scanInfo : virtusScanInfos) {
            String packageName = scanInfo.packageName;
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse(packageName));
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);

        initUI();
        initAnimation();
        checkVirus();
    }

    private void checkVirus() {
        new Thread(){

            private int index = 0;

            @Override
            public void run() {
                List<String> virtusList = VirusDao.getVirtusList();
                //1.获取手机上面的所有程序md5
                PackageManager pm = getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(
                        PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                pb_bar.setMax(packageInfos.size());
                virtusScanInfos = new ArrayList<>();
                ArrayList<ScanInfo> scanInfos = new ArrayList<>();
                for (PackageInfo packageInfo : packageInfos) {
                    Signature[] signatures = packageInfo.signatures;
                    Signature signature = signatures[0];
                    String s = signature.toCharsString();
                    String encoder = MD5Util.encode(s);
                    ScanInfo scanInfo = new ScanInfo();
                    if (virtusList.contains(encoder)) {
                        scanInfo.isVirtus = true;
                        virtusScanInfos.add(scanInfo);
                    } else {
                        scanInfo.isVirtus = false;
                        scanInfos.add(scanInfo);
                    }
                    scanInfo.packageName = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    index++;
                    pb_bar.setProgress(index);
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);

                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = Message.obtain();
                msg.what = SCANFINISH;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    class ScanInfo {
        public boolean isVirtus;
        public String packageName;
        public String name;
    }

    private void initAnimation() {
        rotateAnimation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        //指定动画一直选择
//        rotateAnimation.setRepeatMode(RotateAnimation.INFINITE);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setFillAfter(true);
        iv_scanning.startAnimation(rotateAnimation);
    }

    private void initUI() {
        iv_scanning = findViewById(R.id.iv_scanning);
        tv_name = findViewById(R.id.tv_name);
        pb_bar = findViewById(R.id.pb_bar);
        ll_add_text = findViewById(R.id.ll_add_text);
    }
}
